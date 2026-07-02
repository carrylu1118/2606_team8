package com.binair.admin.task;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 一次性任务：从 dfoe_message/dfme_message 的 AIRL.ARPT 中提取目的地回填 flight_master
 * <p>
 * 开关：destination-populate.enabled = true
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "destination-populate.enabled", havingValue = "true")
public class DestinationPopulator implements CommandLineRunner {

    private final JdbcTemplate jdbc;
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void run(String... args) {
        log.info("========================================");
        log.info("目的地数据回填开始");
        log.info("========================================");

        // 1. 从 dfoe_message DFIE 提取
        fillFrom("dfoe_message", "DFIE");

        // 2. 从 dfme_message AIRL 提取
        fillFrom("dfme_message", "AIRL");

        int total = jdbc.queryForObject("SELECT COUNT(*) FROM flight_master WHERE destination_code IS NOT NULL", Integer.class);
        log.info("[目的地] 回填完成，目前共 {} 条有目的地数据", total);
        log.info("========================================");
    }

    private void fillFrom(String table, String styp) {
        int total = countRows(table, styp);
        log.info("[{} {}] {} 条待处理", table, styp, total);
        int updated = 0;
        for (int offset = 0; offset < total; offset += 200) {
            List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT CAST(raw_data AS CHAR) AS raw_data FROM " + table + " WHERE styp=? LIMIT ?,?", styp, offset, 200);
            for (Map<String, Object> row : rows) {
                try {
                    String raw = rawToString(row.get("raw_data"));
                    if (raw == null) continue;
                    JsonNode body = mapper.readTree(raw);
                    String fide = text(body, "FIDE");
                    if (fide == null) continue;

                    JsonNode arpts = body.path("AIRL").path("ARPT");
                    if (!arpts.isArray() || arpts.size() == 0) continue;
                    String destCode = text(arpts.get(arpts.size() - 1), "APCD");
                    if (destCode == null) continue;

                    int n = jdbc.update(
                        "UPDATE flight_master SET destination_code=?, destination=? WHERE fide=? AND destination_code IS NULL",
                        destCode, destCode, fide);
                    if (n > 0) updated++;
                } catch (Exception e) { /* skip */ }
            }
        }
        log.info("[{} {}] 完成: 填了 {} 条目的地", table, styp, updated);
    }

    private int countRows(String table, String styp) {
        Integer c = jdbc.queryForObject("SELECT COUNT(*) FROM " + table + " WHERE styp=?", Integer.class, styp);
        return c != null ? c : 0;
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
}
