package com.binair.admin.listener;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 监听器启动器
 * <p>
 * 在 Spring Boot 启动完成后自动启动 XML 文件夹监控
 */
@Slf4j
@Component
public class XmlFolderListenerRunner implements CommandLineRunner {

    @Value("${xml.path}")
    private String xmlFolderPath;

    private final XmlFolderAlterationListener listener;
    private XmlFolderMonitor monitor;

    public XmlFolderListenerRunner(XmlFolderAlterationListener listener) {
        this.listener = listener;
    }

    @Override
    public void run(String... args) {
        try {
            monitor = new XmlFolderMonitor(xmlFolderPath, listener);
            monitor.start();
            log.info("XML 文件夹监控已启动，监控路径: {}", xmlFolderPath);
        } catch (Exception e) {
            log.error("启动 XML 文件夹监控失败，路径: {}", xmlFolderPath, e);
        }
    }

    /**
     * 应用关闭时停止监控
     */
    @PreDestroy
    public void destroy() {
        if (monitor != null) {
            monitor.stop();
        }
    }
}
