package com.binair.admin.listener;

import com.binair.admin.cache.XmlProcessCache;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * XML 文件夹监控类
 * <p>
 * 启动时优先从 Redis 拿全量文件清单（不走文件系统），首次启动才 walkFileTree 并缓存清单到 Redis。
 * 之后进入 WatchService 事件循环监听新增/修改。
 */
@Slf4j
public class XmlFolderMonitor {

    private final WatchService watchService;
    private final Path rootPath;
    private final XmlFolderAlterationListener listener;
    private final XmlProcessCache cache;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile boolean running = false;

    public XmlFolderMonitor(String folderPath, XmlFolderAlterationListener listener,
                            XmlProcessCache cache) throws IOException {
        this.rootPath = Paths.get(folderPath);
        this.listener = listener;
        this.cache = cache;
        this.watchService = FileSystems.getDefault().newWatchService();

        if (!Files.isDirectory(rootPath)) {
            throw new IllegalArgumentException("监控目录不存在: " + folderPath);
        }
    }

    public void start() {
        if (running) {
            log.warn("监控已在运行中");
            return;
        }
        running = true;

        // 注册所有目录（轻量，仅目录结构）
        try {
            registerDirectory(rootPath);
            Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    registerDirectory(dir);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            log.error("注册目录监控失败", e);
            return;
        }

        if (cache.hasFileList()) {
            // 快速路径：从 Redis 拿文件清单
            scanFromCache();
        } else {
            // 首次启动：文件系统遍历
            scanFromDisk();
        }

        executor.submit(this::processEvents);
    }

    /**
     * 快速路径：Redis 已有全量文件清单，从清单找未处理文件
     */
    private void scanFromCache() {
        Set<String> allPaths = cache.getAllFilePaths();
        AtomicInteger scanned = new AtomicInteger(0);
        AtomicInteger processed = new AtomicInteger(0);
        AtomicInteger skipped = new AtomicInteger(0);

        for (String path : allPaths) {
            scanned.incrementAndGet();
            // 先查缓存，已处理的不调 listener（绕过 @Transactional 开销）
            if (cache.isProcessed(path)) {
                skipped.incrementAndGet();
                continue;
            }
            File file = new File(path);
            if (file.exists() && listener.onFileCreated(file)) {
                processed.incrementAndGet();
            }
        }

        log.info("扫描完成（Redis缓存）: 总数={}, 处理={}, 跳过={}, 监控已启动",
                scanned.get(), processed.get(), skipped.get());
    }

    /**
     * 首次启动：walkFileTree 遍历所有 .xml 并缓存全量路径到 Redis
     */
    private void scanFromDisk() {
        AtomicInteger scanned = new AtomicInteger(0);
        AtomicInteger processed = new AtomicInteger(0);
        AtomicInteger skipped = new AtomicInteger(0);
        Set<String> allPaths = new HashSet<>();

        try {
            Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) {
                    File file = filePath.toFile();
                    if (file.getName().toLowerCase().endsWith(".xml")) {
                        scanned.incrementAndGet();
                        String absPath = file.getAbsolutePath();
                        allPaths.add(absPath);
                        // 先查缓存再决定是否调 listener（绕开 @Transactional）
                        if (cache.isProcessed(absPath)) {
                            skipped.incrementAndGet();
                        } else if (listener.onFileCreated(file)) {
                            processed.incrementAndGet();
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            log.error("文件系统扫描失败", e);
            return;
        }

        cache.storeFilePaths(allPaths);

        log.info("扫描完成（文件系统）: 总数={}, 处理={}, 跳过={}, 监控已启动",
                scanned.get(), processed.get(), skipped.get());
    }

    public void stop() {
        running = false;
        executor.shutdown();
        try {
            if (!executor.awaitTermination(3, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        log.info("目录监控已停止: {}", rootPath);
    }

    private void registerDirectory(Path dir) throws IOException {
        dir.register(watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY);
    }

    private void processEvents() {
        while (running) {
            WatchKey key;
            try {
                key = watchService.poll(3, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            if (key == null) continue;

            Path dir = (Path) key.watchable();
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();
                if (kind == StandardWatchEventKinds.OVERFLOW) continue;

                @SuppressWarnings("unchecked")
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path fullPath = dir.resolve(ev.context());
                File file = fullPath.toFile();

                if (!file.getName().toLowerCase().endsWith(".xml")) continue;

                if (kind == StandardWatchEventKinds.ENTRY_CREATE && file.isDirectory()) {
                    try {
                        registerDirectory(fullPath);
                    } catch (IOException e) {
                        log.error("注册新目录失败: {}", fullPath, e);
                    }
                    continue;
                }

                if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                    log.info("[新增] {}", file.getName());
                    listener.onFileCreated(file);
                    cache.addFilePath(file.getAbsolutePath());
                } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                    log.info("[修改] {}", file.getName());
                    listener.onFileModified(file);
                }
            }

            boolean valid = key.reset();
            if (!valid) {
                log.warn("监控目录已不可访问: {}", dir);
            }
        }
    }
}
