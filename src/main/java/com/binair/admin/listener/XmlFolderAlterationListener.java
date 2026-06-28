package com.binair.admin.listener;

import com.binair.admin.entity.xml.BaseApueMessage;
import com.binair.admin.entity.xml.DfmeAfidMessage;
import com.binair.admin.entity.xml.DfoeDfdeMessage;
import com.binair.admin.service.AfidService;
import com.binair.admin.service.AirportService;
import com.binair.admin.service.DeleteService;
import com.binair.admin.utils.ParseXmlUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * XML 文件夹变更监听器
 * <p>
 * 当监控目录出现新文件时，触发导入流程
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class XmlFolderAlterationListener {

    private final AirportService airportService;
    private final AfidService afidService;
    private final DeleteService deleteService;

    /**
     * 文件创建事件回调
     */
    public void onFileCreated(File file) {
        log.info("检测到新文件: {}", file.getAbsolutePath());
        parseXml(file);
    }

    /**
     * 文件修改事件回调
     */
    public void onFileModified(File file) {
        log.info("检测到文件修改: {}", file.getAbsolutePath());
        parseXml(file);
    }

    /**
     * 解析 XML 并根据消息类型分发到对应 Service
     */
    private void parseXml(File file) {
        try {
            Object obj = ParseXmlUtil.parseXml(file);
            if (obj instanceof BaseApueMessage msg) {
                log.info("→ 类型: BASE-APUE | 机场: {} {}", msg.getApot().getCode(), msg.getApot().getCnnm());
                airportService.save(msg);
                log.info("→ 已写入 airport 表: {}", msg.getApot().getCode());
            } else if (obj instanceof DfmeAfidMessage msg) {
                log.info("→ 类型: DFME-AFID | 航班: {} afid={}", msg.getDflt().getFlid(), msg.getDflt().getAfid());
                afidService.save(msg);
                log.info("→ 已写入 flight 表: {}", msg.getDflt().getFlid());
            } else if (obj instanceof DfoeDfdeMessage msg) {
                log.info("→ 类型: DFOE-DFDE | 航班: {}", msg.getDflt().getFlid());
                deleteService.save(msg);
                log.info("→ 已写入 flight 表: {}", msg.getDflt().getFlid());
            } else {
                log.warn("→ 未知消息类型，跳过: {}", file.getName());
            }
        } catch (Exception e) {
            log.error("✗ 解析失败: {} - {}", file.getName(), e.getMessage());
        }
    }
}
