package com.binair.admin.service;

import com.binair.admin.entity.DfoeMessage;
import com.binair.admin.entity.xml.Meta;
import com.binair.admin.mapper.DfoeMessageMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * DFOE 消息服务 — DFIE/DFDL/DFDE 统一写入 dfoe_message 表
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DfoeMessageService {

    private final DfoeMessageMapper dfoeMessageMapper;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 保存 DFOE 消息
     */
    public void save(Meta meta, String bodyJson) {
        DfoeMessage entity = new DfoeMessage();

        // Meta 字段
        entity.setSndr(meta.getSndr());
        entity.setRcvr(meta.getRcvr());
        entity.setSeqn(meta.getSeqn());
        entity.setDdtm(meta.getDdtm());
        entity.setType(meta.getType());
        entity.setStyp(meta.getStyp());

        // 从 body JSON 提取通用字段
        if (bodyJson != null) {
            try {
                JsonNode body = objectMapper.readTree(bodyJson);
                entity.setFlid(nullableText(body, "FLID"));
                entity.setFfid(nullableText(body, "FFID"));
                entity.setFide(nullableText(body, "FIDE"));
                entity.setDltp(nullableText(body, "DLTP"));
                entity.setRecd(nullableText(body, "RECD"));
                entity.setRawData(bodyJson);
            } catch (Exception e) {
                log.warn("提取 body 字段失败: {}", e.getMessage());
                entity.setRawData(bodyJson);
            }
        }

        entity.setCreateTime(LocalDateTime.now());
        dfoeMessageMapper.insert(entity);
        log.info("DFOE-{} 写入: flid={}", entity.getStyp(), entity.getFlid());
    }

    private String nullableText(JsonNode node, String fieldName) {
        JsonNode field = node.get(fieldName);
        return (field != null && !field.isNull()) ? field.asText() : null;
    }
}
