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

    /**
     * 保存 BASE 消息
     */
    public void save(Meta meta, String bodyJson) {
        BaseMessage entity = new BaseMessage();

        // Meta 字段
        entity.setSndr(meta.getSndr());
        entity.setRcvr(meta.getRcvr());
        entity.setSeqn(meta.getSeqn());
        entity.setDdtm(meta.getDdtm());
        entity.setType(meta.getType());
        entity.setStyp(meta.getStyp());

        // 先全部置 null，避免 APUE 的 CODE 污染 CFIE 的 cfCode，反之亦然
        entity.setCode(null);
        entity.setFrcd(null);
        entity.setApat(null);
        entity.setCnnm(null);
        entity.setEnnm(null);
        entity.setAiso(null);
        entity.setApsn(null);
        entity.setCfCode(null);
        entity.setCfTp(null);
        entity.setAwcd(null);
        entity.setStnm(null);
        entity.setRstn(null);

        if (bodyJson != null) {
            try {
                JsonNode body = objectMapper.readTree(bodyJson);
                String styp = meta.getStyp();

                if ("APUE".equalsIgnoreCase(styp)) {
                    // APUE：从 APOT 取值 → 机场字段
                    entity.setCode(nullableText(body, "CODE"));
                    entity.setFrcd(nullableText(body, "FRCD"));
                    entity.setApat(nullableText(body, "APAT"));
                    entity.setCnnm(nullableText(body, "CNNM"));
                    entity.setEnnm(nullableText(body, "ENNM"));
                    entity.setAiso(nullableText(body, "AISO"));
                    entity.setApsn(nullableText(body, "APSN"));
                } else if ("CFIE".equalsIgnoreCase(styp) || "CFUE".equalsIgnoreCase(styp)) {
                    // CFIE/CFUE：从 CRFT 取值 → 飞机字段
                    entity.setCfCode(nullableText(body, "CODE"));
                    entity.setCfTp(nullableText(body, "CFTP"));
                    entity.setAwcd(nullableText(body, "AWCD"));
                    entity.setStnm(nullableText(body, "STNM"));
                    entity.setRstn(nullableText(body, "RSTN"));
                }
                // 其他未知 styp 只保留 raw_data，列字段全 null

                entity.setRawData(bodyJson);
            } catch (Exception e) {
                log.warn("提取 body 字段失败: {}", e.getMessage());
                entity.setRawData(bodyJson);
            }
        }

        entity.setCreateTime(LocalDateTime.now());
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
