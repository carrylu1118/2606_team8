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

    public void save(Meta meta, String bodyJson) {
        DfmeMessage.DfmeMessageBuilder builder = DfmeMessage.builder()
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
                        .awcd(nullableText(body, "AWCD"))
                        .flno(nullableText(body, "FLNO"))
                        .mfid(nullableText(body, "MFID"))
                        .mffi(nullableText(body, "MFFI"))
                        .rawData(bodyJson);
            } catch (Exception e) {
                log.warn("提取 body 字段失败: {}", e.getMessage());
                builder.rawData(bodyJson);
            }
        }

        DfmeMessage entity = builder.build();
        dfmeMessageMapper.insert(entity);
        log.info("DFME-{} 写入: flid={}", entity.getStyp(), entity.getFlid());
    }

    private String nullableText(JsonNode node, String fieldName) {
        JsonNode field = node.get(fieldName);
        return (field != null && !field.isNull()) ? field.asText() : null;
    }
}
