package com.binair.admin.listener;

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

    @Transactional
    public void onFileCreated(File file) {
        processFile(file);
    }

    @Transactional
    public void onFileModified(File file) {
        processFile(file);
    }

    private void processFile(File file) {
        String filePath = file.getAbsolutePath();

        // 检查是否已处理（表不存在等异常不阻断流程）
        if (isAlreadyProcessed(filePath)) {
            log.info("  → 跳过（已处理）: {}", file.getName());
            return;
        }

        // 解析
        XmlMessage msg = XmlParser.parse(file);
        if (msg == null || msg.getMeta() == null) {
            log.warn("  → 解析失败: {}", file.getName());
            safeRecord(file, 2);
            return;
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
                return;
            }
            safeRecord(file, 1);
        } catch (Exception e) {
            log.error("  → 入库失败: {} - {}", file.getName(), e.getMessage());
            safeRecord(file, 2);
        }
    }

    /**
     * 查是否已处理，查表异常时返回 false 让流程继续
     */
    private boolean isAlreadyProcessed(String filePath) {
        try {
            XmlProcessRecord existing = processRecordMapper.selectOne(
                    new LambdaQueryWrapper<XmlProcessRecord>().eq(XmlProcessRecord::getFilePath, filePath)
            );
            return existing != null && existing.getProcessStatus() == 1;
        } catch (DataAccessException e) {
            // 表不存在等情况，当作未处理，继续导入
            log.debug("查询处理记录异常（将继续处理）: {}", e.getMessage());
            return false;
        }
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
                XmlProcessRecord record = new XmlProcessRecord();
                record.setFileName(file.getName());
                record.setFilePath(filePath);
                record.setFileSize(file.length());
                record.setFileLastModified(file.lastModified());
                record.setProcessStatus(status);
                record.setProcessTime(LocalDateTime.now());
                processRecordMapper.insert(record);
            }
        } catch (DataAccessException e) {
            log.debug("写入处理记录异常: {}", e.getMessage());
        }
    }
}
