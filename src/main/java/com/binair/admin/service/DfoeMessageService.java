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

    public void save(Meta meta, String bodyJson) {
        DfoeMessage.DfoeMessageBuilder builder = DfoeMessage.builder()
                .sndr(meta.getSndr())
                .rcvr(meta.getRcvr())
                .seqn(meta.getSeqn())
                .ddtm(meta.getDdtm())
                .type(meta.getType())
                .styp(meta.getStyp())
                .createTime(LocalDateTime.now());

        if (bodyJson != null) {
            try {
                JsonNode body = objectMapper.readTree(bodyJson);
                builder.flid(nullableText(body, "FLID"))
                        .ffid(nullableText(body, "FFID"))
                        .fide(nullableText(body, "FIDE"))
                        .dltp(nullableText(body, "DLTP"))
                        .recd(nullableText(body, "RECD"))
                        .rawData(bodyJson);
            } catch (Exception e) {
                log.warn("提取 body 字段失败: {}", e.getMessage());
                builder.rawData(bodyJson);
            }
        }

        DfoeMessage entity = builder.build();
        dfoeMessageMapper.insert(entity);
        log.info("DFOE-{} 写入: flid={}", entity.getStyp(), entity.getFlid());
    }

    private String nullableText(JsonNode node, String fieldName) {
        JsonNode field = node.get(fieldName);
        return (field != null && !field.isNull()) ? field.asText() : null;
    }
}
