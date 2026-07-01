package com.binair.admin.task;

import com.binair.admin.mapper.DfmeBusinessMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 一次性任务：从 dfme_message 的 raw_data 中提取 GTLS / CKLS 明细
 * <p>
 * 写入 DELT_GATE（登机门明细）和 CKLS_CNTR（值机柜台明细）
 * <p>
 * 开关：application.yml 中 gtls-ckls-extract.enabled = true 时启动执行
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "gtls-ckls-extract.enabled", havingValue = "true")
public class GtlsCklsDetailExtractor implements CommandLineRunner {

    private final JdbcTemplate jdbc;
    private final DfmeBusinessMapper dfmeMapper;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final int PAGE_SIZE = 200;

    @Override
    public void run(String... args) {
        log.info("========================================");
        log.info("GTLS / CKLS 明细提取开始");
        log.info("========================================");

        try {
            extractGtls();
            extractCkls();
        } catch (Exception e) {
            log.error("明细提取异常", e);
        }

        log.info("========================================");
        log.info("GTLS / CKLS 明细提取结束");
        log.info("========================================");
    }

    // ==================== GTLS → DELT_GATE ====================

    private void extractGtls() {
        log.info("[GTLS] 开始处理...");
        int total = countDfmeByStyp("GTLS");
        log.info("[GTLS] dfme_message 中共 {} 条记录", total);
        if (total == 0) { log.warn("[GTLS] 没有数据，跳过"); return; }

        int processed = 0;
        int skippedNoMaster = 0;
        int skippedExists = 0;
        int inserted = 0;
        int errorCount = 0;

        for (int offset = 0; offset < total; offset += PAGE_SIZE) {
            List<Map<String, Object>> rows = queryDfmeByStyp("GTLS", offset, PAGE_SIZE);
            for (Map<String, Object> row : rows) {
                processed++;
                try {
                    String rawData = rawToString(row.get("raw_data"));
                    String fide = (String) row.get("fide");
                    if (rawData == null || fide == null) {
                        skippedNoMaster++;
                        continue;
                    }

                    // 1. 解析 JSON，提取 GATE 数组（兼容单元素被解析为对象的情况）
                    JsonNode root = objectMapper.readTree(rawData);
                    JsonNode gates = toArray(root.path("GTLS").path("GATE"));
                    if (gates.size() == 0) {
                        continue;
                    }

                    // 2. 查 GTLS_DELT 主表获取 master_id
                    Long masterId = queryMasterId("GTLS_DELT", fide);
                    if (masterId == null) {
                        skippedNoMaster++;
                        continue;
                    }

                    // 3. 防重复
                    if (existsInDetail("DELT_GATE", masterId)) {
                        skippedExists++;
                        continue;
                    }

                    // 4. 逐条插入
                    for (JsonNode gate : gates) {
                        Map<String, Object> p = new HashMap<>();
                        p.put("masterId", masterId);
                        p.put("gtno", parseInt(gate, "GTNO"));
                        p.put("code", text(gate, "CODE"));
                        p.put("gtat", text(gate, "GTAT"));
                        p.put("estr", text(gate, "ESTR"));
                        p.put("eend", text(gate, "EEND"));
                        p.put("rstr", text(gate, "RSTR"));
                        p.put("rend", text(gate, "REND"));
                        p.put("btsc", text(gate, "BTSC"));
                        dfmeMapper.insertGtlsGate(p);
                        inserted++;
                    }
                } catch (Exception e) {
                    errorCount++;
                    log.warn("[GTLS] 处理异常 row={}: {}", row.get("id"), e.getMessage());
                }
            }
            if (processed % 100 == 0 || offset + PAGE_SIZE >= total) {
                log.info("[GTLS] 进度: {}/{} | 已插入 {} 条明细 | 跳过(无主表={}, 已存在={}) | 错误={}",
                        processed, total, inserted, skippedNoMaster, skippedExists, errorCount);
            }
        }
        log.info("[GTLS] 完成: 处理 {} 条, 插入 {} 条明细, 跳过(无主表={}, 已存在={}), 错误={}",
                processed, inserted, skippedNoMaster, skippedExists, errorCount);
    }

    // ==================== CKLS → CKLS_CNTR ====================

    private void extractCkls() {
        log.info("[CKLS] 开始处理...");
        int total = countDfmeByStyp("CKLS");
        log.info("[CKLS] dfme_message 中共 {} 条记录", total);
        if (total == 0) { log.warn("[CKLS] 没有数据，跳过"); return; }

        int processed = 0;
        int skippedNoMaster = 0;
        int skippedExists = 0;
        int inserted = 0;
        int errorCount = 0;

        for (int offset = 0; offset < total; offset += PAGE_SIZE) {
            List<Map<String, Object>> rows = queryDfmeByStyp("CKLS", offset, PAGE_SIZE);
            for (Map<String, Object> row : rows) {
                processed++;
                try {
                    String rawData = rawToString(row.get("raw_data"));
                    String fide = (String) row.get("fide");
                    if (rawData == null || fide == null) {
                        skippedNoMaster++;
                        continue;
                    }

                    // 1. 解析 JSON，提取 CNTR 数组（兼容单元素被解析为对象的情况）
                    JsonNode root = objectMapper.readTree(rawData);
                    JsonNode cntrs = toArray(root.path("CKLS").path("CNTR"));
                    if (cntrs.size() == 0) {
                        continue;
                    }

                    // 2. 查 CKLS_DELT 主表获取 master_id
                    Long masterId = queryMasterId("CKLS_DELT", fide);
                    if (masterId == null) {
                        skippedNoMaster++;
                        continue;
                    }

                    // 3. 防重复
                    if (existsInDetail("CKLS_CNTR", masterId)) {
                        skippedExists++;
                        continue;
                    }

                    // 4. 逐条插入
                    for (JsonNode cntr : cntrs) {
                        Map<String, Object> p = new HashMap<>();
                        p.put("masterId", masterId);
                        p.put("ckno", parseInt(cntr, "CKNO"));
                        p.put("code", text(cntr, "CODE"));
                        p.put("ckat", text(cntr, "CKAT"));
                        p.put("type", text(cntr, "TYPE"));
                        p.put("ccar", text(cntr, "CCAR"));
                        p.put("estr", text(cntr, "ESTR"));
                        p.put("eend", text(cntr, "EEND"));
                        p.put("rstr", text(cntr, "RSTR"));
                        p.put("rend", text(cntr, "REND"));
                        p.put("btsc", text(cntr, "BTSC"));
                        dfmeMapper.insertCklsCntr(p);
                        inserted++;
                    }
                } catch (Exception e) {
                    errorCount++;
                    log.warn("[CKLS] 处理异常 row={}: {}", row.get("id"), e.getMessage());
                }
            }
            if (processed % 100 == 0 || offset + PAGE_SIZE >= total) {
                log.info("[CKLS] 进度: {}/{} | 已插入 {} 条明细 | 跳过(无主表={}, 已存在={}) | 错误={}",
                        processed, total, inserted, skippedNoMaster, skippedExists, errorCount);
            }
        }
        log.info("[CKLS] 完成: 处理 {} 条, 插入 {} 条明细, 跳过(无主表={}, 已存在={}), 错误={}",
                processed, inserted, skippedNoMaster, skippedExists, errorCount);
    }

    // ==================== 数据库查询辅助 ====================

    private int countDfmeByStyp(String styp) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM dfme_message WHERE styp = ?", Integer.class, styp);
        return count != null ? count : 0;
    }

    private List<Map<String, Object>> queryDfmeByStyp(String styp, int offset, int limit) {
        return jdbc.queryForList(
                "SELECT id, fide, CAST(raw_data AS CHAR) AS raw_data FROM dfme_message WHERE styp = ? LIMIT ?, ?",
                styp, offset, limit);
    }

    /** 将 JdbcTemplate 返回的 raw_data（可能为 byte[] 或 String）转为字符串 */
    private String rawToString(Object raw) {
        if (raw == null) return null;
        if (raw instanceof String) return (String) raw;
        if (raw instanceof byte[]) return new String((byte[]) raw);
        return raw.toString();
    }

    /** 根据 FIDE 查询主表 ID */
    private Long queryMasterId(String table, String fide) {
        try {
            return jdbc.queryForObject(
                    "SELECT id FROM " + table + " WHERE fide = ? LIMIT 1",
                    Long.class, fide);
        } catch (Exception e) {
            return null;
        }
    }

    /** 检查明细表是否已有该主表 ID 的数据 */
    private boolean existsInDetail(String table, Long masterId) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM " + table + " WHERE master_id = ?",
                Integer.class, masterId);
        return count != null && count > 0;
    }

    // ==================== JSON 读取辅助 ====================

    /**
     * 将 JsonNode 转为数组（兼容 XML 解析后单元素为对象的情况）
     */
    private JsonNode toArray(JsonNode node) {
        if (node.isMissingNode() || node.isNull()) {
            return objectMapper.createArrayNode();
        }
        if (node.isArray()) {
            return node;
        }
        // 单个对象 → 包装成数组
        return objectMapper.createArrayNode().add(node);
    }

    private String text(JsonNode node, String fieldName) {
        JsonNode field = node.get(fieldName);
        return (field != null && !field.isNull() && !field.asText().isEmpty()) ? field.asText() : null;
    }

    private Integer parseInt(JsonNode node, String fieldName) {
        JsonNode field = node.get(fieldName);
        if (field != null && !field.isNull()) {
            try {
                return field.asInt();
            } catch (Exception e) {
                try {
                    return Integer.parseInt(field.asText().trim());
                } catch (Exception ignored) {}
            }
        }
        return null;
    }
}
