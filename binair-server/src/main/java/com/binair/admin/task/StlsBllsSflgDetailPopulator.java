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
 * 一次性任务：从 dfme_message.raw_data 提取 STLS/BLLS/SFLG 明细
 * <p>
 * STLS → TB_STLSINFO（机位明细）
 * BLLS → BLLS_DFLTDETAIL（行李转盘明细）
 * SFLG → TB_SFLGINFO（共享航班明细）
 * <p>
 * 开关：stls-blls-sflg-populate.enabled = true
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "stls-blls-sflg-populate.enabled", havingValue = "true")
public class StlsBllsSflgDetailPopulator implements CommandLineRunner {

    private final JdbcTemplate jdbc;
    private final DfmeBusinessMapper dfmeMapper;
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final int BATCH = 500;

    @Override
    public void run(String... args) {
        log.info("========================================");
        log.info("STLS/BLLS/SFLG 明细提取开始");
        log.info("========================================");

        extractStls();
        extractBlls();
        extractSflg();

        log.info("========================================");
        log.info("STLS/BLLS/SFLG 明细提取结束");
        log.info("========================================");
    }

    // ==================== STLS → TB_STLSINFO ====================

    private void extractStls() {
        int total = countDfme("STLS");
        log.info("[STLS] {} 条", total);
        if (total == 0) return;

        int processed = 0, inserted = 0, skippedNoMaster = 0, skippedExists = 0, errors = 0;
        for (int offset = 0; offset < total; offset += BATCH) {
            List<Map<String, Object>> rows = queryDfme("STLS", offset, BATCH);
            for (Map<String, Object> row : rows) {
                processed++;
                try {
                    String raw = rawToString(row.get("raw_data"));
                    if (raw == null) continue;
                    JsonNode body = mapper.readTree(raw);
                    String fide = text(body, "FIDE");
                    if (fide == null) continue;

                    JsonNode stands = toArray(body.path("STLS").path("STAND"));
                    if (stands.size() == 0) continue;

                    Long masterId = queryMasterId("TB_STLSBODY", fide);
                    if (masterId == null) { skippedNoMaster++; continue; }
                    if (existsDetail("TB_STLSINFO", masterId)) { skippedExists++; continue; }

                    for (JsonNode s : stands) {
                        Map<String, Object> p = new HashMap<>();
                        p.put("masterId", masterId);
                        p.put("stno", parseInt(s, "STNO"));
                        p.put("code", text(s, "CODE"));
                        p.put("estr", text(s, "ESTR"));
                        p.put("eend", text(s, "EEND"));
                        p.put("rstr", text(s, "RSTR"));
                        p.put("rend", text(s, "REND"));
                        p.put("btsc", text(s, "BTSC"));
                        p.put("cssi", text(s, "CSSI"));
                        dfmeMapper.insertStlsInfo(p);
                        inserted++;
                    }
                } catch (Exception e) { errors++; }
            }
            if (processed % 2000 == 0 || offset + BATCH >= total)
                log.info("[STLS] 进度: {}/{} 插入明细={} 跳过(无主表={},已存在={}) 错误={}",
                    processed, total, inserted, skippedNoMaster, skippedExists, errors);
        }
        log.info("[STLS] 完成: 插入={} 明细", inserted);
    }

    // ==================== BLLS → BLLS_DFLTDETAIL ====================

    private void extractBlls() {
        int total = countDfme("BLLS");
        log.info("[BLLS] {} 条", total);
        if (total == 0) return;

        int processed = 0, inserted = 0, skippedNoMaster = 0, skippedExists = 0, errors = 0;
        for (int offset = 0; offset < total; offset += BATCH) {
            List<Map<String, Object>> rows = queryDfme("BLLS", offset, BATCH);
            for (Map<String, Object> row : rows) {
                processed++;
                try {
                    String raw = rawToString(row.get("raw_data"));
                    if (raw == null) continue;
                    JsonNode body = mapper.readTree(raw);
                    String fide = text(body, "FIDE");
                    if (fide == null) continue;

                    JsonNode belts = toArray(body.path("BLLS").path("BELT"));
                    if (belts.size() == 0) continue;

                    Long masterId = queryMasterId("BLLS_DFLT", fide);
                    if (masterId == null) { skippedNoMaster++; continue; }
                    if (existsDetail("BLLS_DFLTDETAIL", masterId)) { skippedExists++; continue; }

                    for (JsonNode b : belts) {
                        Map<String, Object> p = new HashMap<>();
                        p.put("masterId", masterId);
                        p.put("btno", parseInt(b, "BTNO"));
                        p.put("code", text(b, "CODE"));
                        p.put("btat", text(b, "BTAT"));
                        p.put("estr", text(b, "ESTR"));
                        p.put("eend", text(b, "EEND"));
                        p.put("rstr", text(b, "RSTR"));
                        p.put("rend", text(b, "REND"));
                        p.put("btsc", text(b, "BTSC"));
                        p.put("exno", text(b, "EXNO"));
                        dfmeMapper.insertBllsDetail(p);
                        inserted++;
                    }
                } catch (Exception e) { errors++; }
            }
            if (processed % 2000 == 0 || offset + BATCH >= total)
                log.info("[BLLS] 进度: {}/{} 插入明细={} 跳过(无主表={},已存在={}) 错误={}",
                    processed, total, inserted, skippedNoMaster, skippedExists, errors);
        }
        log.info("[BLLS] 完成: 插入={} 明细", inserted);
    }

    // ==================== SFLG → TB_SFLGINFO ====================

    private void extractSflg() {
        int total = countDfme("SFLG");
        log.info("[SFLG] {} 条", total);
        if (total == 0) return;

        int processed = 0, inserted = 0, skippedNoMaster = 0, skippedExists = 0, errors = 0;
        for (int offset = 0; offset < total; offset += BATCH) {
            List<Map<String, Object>> rows = queryDfme("SFLG", offset, BATCH);
            for (Map<String, Object> row : rows) {
                processed++;
                try {
                    String raw = rawToString(row.get("raw_data"));
                    if (raw == null) continue;
                    JsonNode body = mapper.readTree(raw);
                    String fide = text(body, "FIDE");
                    if (fide == null) continue;

                    JsonNode sflgs = toArray(body.path("SFLG").path("SFLGINFO"));
                    if (sflgs.size() == 0) continue;

                    Long masterId = queryMasterId("TB_SFLGBODY", fide);
                    if (masterId == null) { skippedNoMaster++; continue; }
                    if (existsDetail("TB_SFLGINFO", masterId)) { skippedExists++; continue; }

                    for (JsonNode sf : sflgs) {
                        Map<String, Object> p = new HashMap<>();
                        p.put("masterId", masterId);
                        p.put("sfaw", text(sf, "SFAW"));
                        p.put("sfno", text(sf, "SFNO"));
                        dfmeMapper.insertSflgInfo(p);
                        inserted++;
                    }
                } catch (Exception e) { errors++; }
            }
            if (processed % 2000 == 0 || offset + BATCH >= total)
                log.info("[SFLG] 进度: {}/{} 插入明细={} 跳过(无主表={},已存在={}) 错误={}",
                    processed, total, inserted, skippedNoMaster, skippedExists, errors);
        }
        log.info("[SFLG] 完成: 插入={} 明细", inserted);
    }

    // ==================== 辅助 ====================

    private int countDfme(String styp) {
        Integer c = jdbc.queryForObject("SELECT COUNT(*) FROM dfme_message WHERE styp=?", Integer.class, styp);
        return c != null ? c : 0;
    }

    private List<Map<String, Object>> queryDfme(String styp, int offset, int limit) {
        return jdbc.queryForList(
            "SELECT id, fide, CAST(raw_data AS CHAR) AS raw_data FROM dfme_message WHERE styp=? LIMIT ?,?", styp, offset, limit);
    }

    private Long queryMasterId(String table, String fide) {
        try {
            return jdbc.queryForObject("SELECT id FROM " + table + " WHERE fide=? LIMIT 1", Long.class, fide);
        } catch (Exception e) { return null; }
    }

    private boolean existsDetail(String table, Long masterId) {
        Integer c = jdbc.queryForObject("SELECT COUNT(*) FROM " + table + " WHERE master_id=?", Integer.class, masterId);
        return c != null && c > 0;
    }

    private JsonNode toArray(JsonNode node) {
        if (node.isMissingNode() || node.isNull()) return mapper.createArrayNode();
        if (node.isArray()) return node;
        return mapper.createArrayNode().add(node);
    }

    private String rawToString(Object raw) {
        if (raw == null) return null;
        if (raw instanceof String s) return s;
        if (raw instanceof byte[] b) return new String(b);
        return raw.toString();
    }

    private String text(JsonNode node, String field) {
        JsonNode f = node.get(field);
        return (f != null && !f.isNull() && !f.asText().isEmpty()) ? f.asText() : null;
    }

    private Integer parseInt(JsonNode node, String field) {
        JsonNode f = node.get(field);
        if (f != null && !f.isNull()) {
            try { return f.asInt(); } catch (Exception e) {
                try { return Integer.parseInt(f.asText().trim()); } catch (Exception ignored) {}
            }
        }
        return null;
    }
}
