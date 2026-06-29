package com.binair.admin.listener;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * XML 文件夹监控类
 * <p>
 * 启动时先统计 XML 文件总数，再逐个处理并输出进度（当前/总数）。
 * 之后进入 WatchService 事件循环监听新增/修改。
 */
@Slf4j
public class XmlFolderMonitor {

    private final WatchService watchService;
    private final Path rootPath;
    private final XmlFolderAlterationListener listener;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile boolean running = false;

    public XmlFolderMonitor(String folderPath, XmlFolderAlterationListener listener) throws IOException {
        this.rootPath = Paths.get(folderPath);
        this.listener = listener;
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

        try {
            registerDirectory(rootPath);
            Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    registerDirectory(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
            log.info("开始监控目录: {}", rootPath);
        } catch (IOException e) {
            log.error("初始化目录监控失败", e);
            return;
        }

        scanExistingFiles();
        executor.submit(this::processEvents);
    }

    /**
     * 扫描存量 XML 文件：先统计总数，再带进度逐个处理
     */
    private void scanExistingFiles() {
        log.info("========== 开始扫描存量 XML 文件 ==========");

        // 第一遍：收集所有 .xml 文件路径
        List<File> xmlFiles = new ArrayList<>();
        try {
            Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) {
                    File file = filePath.toFile();
                    if (file.getName().toLowerCase().endsWith(".xml")) {
                        xmlFiles.add(file);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            log.error("扫描文件列表失败", e);
            return;
        }

        int total = xmlFiles.size();
        log.info("共发现 {} 个 XML 文件，开始逐个处理...", total);

        // 第二遍：带进度处理
        AtomicInteger processed = new AtomicInteger(0);
        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger failed = new AtomicInteger(0);

        for (int i = 0; i < xmlFiles.size(); i++) {
            File file = xmlFiles.get(i);
            int current = i + 1;
            log.info("[{}/{}] {}", current, total, file.getName());
            try {
                listener.onFileCreated(file);
                success.incrementAndGet();
            } catch (Exception e) {
                failed.incrementAndGet();
                log.error("[{}/{}] 处理异常: {} - {}", current, total, file.getName(), e.getMessage());
            }
            processed.incrementAndGet();
        }

        log.info("========== 扫描完成: 总数={}, 成功={}, 失败={}, 监控已启动 =========",
                total, success.get(), failed.get());
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
