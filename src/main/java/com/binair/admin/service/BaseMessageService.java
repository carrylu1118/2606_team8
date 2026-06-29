package com.binair.admin.service;

import com.binair.admin.entity.BaseMessage;
import com.binair.admin.entity.xml.Meta;
import com.binair.admin.mapper.BaseMessageMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * BASE 消息服务 — APUE/CFIE/CFUE 统一写入 base_message 表
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BaseMessageService {

    private final BaseMessageMapper baseMessageMapper;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public void save(Meta meta, String bodyJson) {
        BaseMessage.BaseMessageBuilder builder = BaseMessage.builder()
                .sndr(meta.getSndr())
                .rcvr(meta.getRcvr())
                .seqn(meta.getSeqn())
                .ddtm(meta.getDdtm())
                .type(meta.getType())
                .styp(meta.getStyp())
                // 默认全 null，按 styp 填充对应字段
                .code(null).frcd(null).apat(null).cnnm(null).ennm(null).aiso(null).apsn(null)
                .cfCode(null).cfTp(null).awcd(null).stnm(null).rstn(null)
                .createTime(LocalDateTime.now());

        if (bodyJson != null) {
            try {
                JsonNode body = objectMapper.readTree(bodyJson);
                String styp = meta.getStyp();

                if ("APUE".equalsIgnoreCase(styp)) {
                    builder.code(nullableText(body, "CODE"))
                            .frcd(nullableText(body, "FRCD"))
                            .apat(nullableText(body, "APAT"))
                            .cnnm(nullableText(body, "CNNM"))
                            .ennm(nullableText(body, "ENNM"))
                            .aiso(nullableText(body, "AISO"))
                            .apsn(nullableText(body, "APSN"));
                } else if ("CFIE".equalsIgnoreCase(styp) || "CFUE".equalsIgnoreCase(styp)) {
                    builder.cfCode(nullableText(body, "CODE"))
                            .cfTp(nullableText(body, "CFTP"))
                            .awcd(nullableText(body, "AWCD"))
                            .stnm(nullableText(body, "STNM"))
                            .rstn(nullableText(body, "RSTN"));
                }

                builder.rawData(bodyJson);
            } catch (Exception e) {
                log.warn("提取 body 字段失败: {}", e.getMessage());
                builder.rawData(bodyJson);
            }
        }

        BaseMessage entity = builder.build();
        baseMessageMapper.insert(entity);
        log.info("BASE-{} 写入: {}",
                entity.getStyp(),
                entity.getCode() != null ? entity.getCode() : entity.getCfCode());
    }

    private String nullableText(JsonNode node, String fieldName) {
        JsonNode field = node.get(fieldName);
        return (field != null && !field.isNull()) ? field.asText() : null;
    }
}
