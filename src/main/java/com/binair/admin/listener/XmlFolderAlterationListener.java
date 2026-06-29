package com.binair.admin.listener;

import com.binair.admin.cache.XmlProcessCache;
import com.binair.admin.entity.XmlProcessRecord;
import com.binair.admin.entity.xml.Meta;
import com.binair.admin.entity.xml.XmlMessage;
import com.binair.admin.mapper.XmlProcessRecordMapper;
import com.binair.admin.service.BaseMessageService;
import com.binair.admin.service.DfmeMessageService;
import com.binair.admin.service.DfoeMessageService;
import com.binair.admin.utils.XmlParser;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.time.LocalDateTime;

/**
 * XML 文件夹变更监听器
 * <p>
 * 流程：查 xml_process_record → 已处理跳过 → 解析 → 分发到对应 Service → 写记录
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class XmlFolderAlterationListener {

    private final DfmeMessageService dfmeMessageService;
    private final BaseMessageService baseMessageService;
    private final DfoeMessageService dfoeMessageService;
    private final XmlProcessRecordMapper processRecordMapper;
    private final XmlProcessCache processCache;

    /**
     * @return true 表示实际处理了，false 表示跳过
     */
    @Transactional
    public boolean onFileCreated(File file) {
        return processFile(file);
    }

    /**
     * @return true 表示实际处理了，false 表示跳过
     */
    @Transactional
    public boolean onFileModified(File file) {
        return processFile(file);
    }

    /**
     * @return true 处理了，false 跳过
     */
    private boolean processFile(File file) {
        // 检查是否已处理（走本地 HashSet 缓存，O(1)）
        if (processCache.isProcessed(file)) {
            log.debug("  → 跳过（已处理）: {}", file.getName());
            return false;
        }

        // 解析
        XmlMessage msg = XmlParser.parse(file);
        if (msg == null || msg.getMeta() == null) {
            log.warn("  → 解析失败: {}", file.getName());
            safeRecord(file, 2);
            return true;
        }

        Meta meta = msg.getMeta();
        String type = meta.getType();
        String bodyJson = msg.getBodyJson();

        try {
            if ("DFME".equalsIgnoreCase(type)) {
                dfmeMessageService.save(meta, bodyJson);
                log.info("  → DFME-{} 已写入", meta.getStyp());
            } else if ("BASE".equalsIgnoreCase(type)) {
                baseMessageService.save(meta, bodyJson);
                log.info("  → BASE-{} 已写入", meta.getStyp());
            } else if ("DFOE".equalsIgnoreCase(type)) {
                dfoeMessageService.save(meta, bodyJson);
                log.info("  → DFOE-{} 已写入", meta.getStyp());
            } else {
                log.warn("  → 未知 TYPE: {}", type);
                safeRecord(file, 2);
                return true;
            }
            processCache.markProcessed(file);
            safeRecord(file, 1);
        } catch (Exception e) {
            log.error("  → 入库失败: {} - {}", file.getName(), e.getMessage());
            safeRecord(file, 2);
        }
        return true;
    }

    /**
     * 安全写记录，表异常不影响导入
     */
    private void safeRecord(File file, int status) {
        try {
            String filePath = file.getAbsolutePath();
            XmlProcessRecord existing = processRecordMapper.selectOne(
                    new LambdaQueryWrapper<XmlProcessRecord>().eq(XmlProcessRecord::getFilePath, filePath)
            );

            if (existing != null) {
                existing.setProcessStatus(status);
                existing.setProcessTime(LocalDateTime.now());
                processRecordMapper.updateById(existing);
            } else {
                XmlProcessRecord record = XmlProcessRecord.builder()
                        .fileName(file.getName())
                        .filePath(filePath)
                        .fileSize(file.length())
                        .fileLastModified(file.lastModified())
                        .processStatus(status)
                        .processTime(LocalDateTime.now())
                        .build();
                processRecordMapper.insert(record);
            }
        } catch (DataAccessException e) {
            log.debug("写入处理记录异常: {}", e.getMessage());
        }
    }
}
