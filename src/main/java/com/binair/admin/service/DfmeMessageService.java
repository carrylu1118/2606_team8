package com.binair.admin.service;

import com.binair.admin.entity.DfmeMessage;
import com.binair.admin.entity.xml.Meta;
import com.binair.admin.mapper.DfmeMessageMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * DFME 消息服务 — 所有 DFME-* 子类型统一写入 dfme_message 表
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DfmeMessageService {

    private final DfmeMessageMapper dfmeMessageMapper;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 保存 DFME 消息
     */
    public void save(Meta meta, String bodyJson) {
        DfmeMessage entity = new DfmeMessage();

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
                entity.setAwcd(nullableText(body, "AWCD"));
                entity.setFlno(nullableText(body, "FLNO"));
                entity.setMfid(nullableText(body, "MFID"));
                entity.setMffi(nullableText(body, "MFFI"));
                entity.setRawData(bodyJson);
            } catch (Exception e) {
                log.warn("提取 body 字段失败: {}", e.getMessage());
                entity.setRawData(bodyJson);
            }
        }

        entity.setCreateTime(LocalDateTime.now());
        dfmeMessageMapper.insert(entity);
        log.info("DFME-{} 写入: flid={}", entity.getStyp(), entity.getFlid());
    }

    private String nullableText(JsonNode node, String fieldName) {
        JsonNode field = node.get(fieldName);
        return (field != null && !field.isNull()) ? field.asText() : null;
    }
}
