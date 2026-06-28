package com.binair.admin.listener;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * XML 文件夹监控类
 * <p>
 * 使用 Java WatchService 监听指定目录下 XML 文件的新增和修改事件。
 * 启动时会先扫描已有子目录并注册监听。
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

        // 校验目录是否存在
        if (!Files.isDirectory(rootPath)) {
            throw new IllegalArgumentException("监控目录不存在: " + folderPath);
        }
    }

    /**
     * 启动监控：扫描已有子目录并注册，然后开始事件循环
     */
    public void start() {
        if (running) {
            log.warn("监控已在运行中");
            return;
        }
        running = true;

        try {
            // 注册根目录
            registerDirectory(rootPath);
            // 扫描并注册已有的子目录
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

        // 先扫描存量 XML 文件，再进入事件监听
        scanExistingFiles();

        // 后台线程处理文件事件
        executor.submit(this::processEvents);
    }

    /**
     * 扫描目录下已有的 XML 文件，逐个交给监听器处理
     */
    private void scanExistingFiles() {
        log.info("========== 开始扫描存量 XML 文件 ==========");
        log.info("根目录: {}", rootPath);

        try {
            final int[] count = {0};
            Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) {
                    File file = filePath.toFile();
                    if (file.getName().toLowerCase().endsWith(".xml")) {
                        count[0]++;
                        log.info("[{}/?] 发现存量文件: {}", count[0], file.getAbsolutePath());
                        listener.onFileCreated(file);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    return FileVisitResult.CONTINUE;
                }
            });
            log.info("========== 扫描完成，共处理 {} 个 XML 文件，监控已启动 ==========", count[0]);
        } catch (IOException e) {
            log.error("扫描存量文件失败", e);
        }
    }

    /**
     * 停止监控
     */
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

    /**
     * 注册目录到 WatchService
     */
    private void registerDirectory(Path dir) throws IOException {
        dir.register(watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY);
    }

    /**
     * 事件处理循环
     */
    private void processEvents() {
        while (running) {
            WatchKey key;
            try {
                key = watchService.poll(3, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            if (key == null) {
                continue;
            }

            Path dir = (Path) key.watchable();
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                // 跳过溢出事件
                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }

                @SuppressWarnings("unchecked")
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path fileName = ev.context();
                Path fullPath = dir.resolve(fileName);

                File file = fullPath.toFile();

                // 只处理 .xml 文件
                if (!file.getName().toLowerCase().endsWith(".xml")) {
                    continue;
                }

                // 如果是新目录，注册它
                if (kind == StandardWatchEventKinds.ENTRY_CREATE && file.isDirectory()) {
                    try {
                        registerDirectory(fullPath);
                    } catch (IOException e) {
                        log.error("注册新目录失败: {}", fullPath, e);
                    }
                    continue;
                }

                // 回调监听器
                if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                    listener.onFileCreated(file);
                } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
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
