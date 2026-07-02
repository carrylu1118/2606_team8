package com.binair.admin.task;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 一次性任务：从 dfme_message + dfoe_message 全量构建 flight_master
 * <p>
 * 阶段1：遍历 dfme_message 中所有唯一 FIDE，取最早事件的 raw_data 提取基本信息创建航班
 * 阶段2：按时间顺序处理 DFME 事件更新状态/时间
 * <p>
 * 开关：flight-master-bootstrap.enabled = true
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "flight-master-bootstrap.enabled", havingValue = "true")
public class FlightMasterBootstrap implements CommandLineRunner {

    private final JdbcTemplate jdbc;
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final int BATCH = 500;

    @Override
    public void run(String... args) {
        log.info("========================================");
        log.info("FlightMaster 数据灌入开始 (dfme全量模式)");
        log.info("========================================");

        // 清空已有数据
        jdbc.update("DELETE FROM flight_master");
        log.info("已清空 flight_master");

        phase1CreateFlights();
        phase1bFixFromFide();
        phase2UpdateStatus();

        log.info("========================================");
        log.info("FlightMaster 数据灌入结束");
        log.info("========================================");
    }

    // ==================== 阶段1：全量创建航班 ====================

    private void phase1CreateFlights() {
        log.info("[阶段1] 从 dfme_message 提取所有唯一 FIDE 建航班...");

        // 获取所有唯一 FIDE
        List<String> allFides = jdbc.queryForList(
            "SELECT DISTINCT fide FROM dfme_message WHERE fide IS NOT NULL", String.class);
        log.info("[阶段1] 共 {} 个唯一 FIDE", allFides.size());

        int inserted = 0, skipped = 0, errors = 0;
        for (int i = 0; i < allFides.size(); i++) {
            String fide = allFides.get(i);
            try {
                // FIDE 格式: 航司-航班号-计划时间14位-方向 (如 HU-7160-20170531233000-D)
                // 基本信息直接从 FIDE 解析，不需要 raw_data
                String[] fideParts = fide.split("-");
                String airline = fideParts.length > 0 ? fideParts[0] : null;
                String flightNo = fideParts.length > 1 ? fideParts[1] : null;
                String planDepartStr = fideParts.length > 2 ? fideParts[2].substring(0, 14) : null;
                String departure = "天津";
                String destCode = null;
                String planArriveStr = null;
                String flid = null;

                // 从最佳事件提取额外信息（目的地等）
                Map<String, Object> row = queryBestEvent(fide);
                String raw = rawToString(row.get("raw_data"));
                if (raw != null) {
                    JsonNode body = mapper.readTree(raw);
                    flid = text(body, "FLID");
                    // 尝试从 AIRL 提取目的地
                    JsonNode arpts = body.path("AIRL").path("ARPT");
                    if (arpts.isArray() && arpts.size() > 0) {
                        JsonNode last = arpts.get(arpts.size() - 1);
                        destCode = text(last, "APCD");
                        planArriveStr = text(last, "FPLT");
                    }
                    // 计划时间优先用 raw_data 中的 FPTT
                    String fptt = text(body, "FPTT");
                    if (fptt != null) planDepartStr = fptt;
                }

                jdbc.update(
                    "INSERT INTO flight_master (fide, flid, flight_no, airline, departure, " +
                    "destination_code, destination, plan_depart_time, plan_arrive_time, " +
                    "status_code, status_name, create_time) VALUES (?,?,?,?,?,?,?,?,?,'PLAN','计划中',NOW())",
                    fide, flid, flightNo, airline, departure,
                    destCode, destCode,
                    parseDt(planDepartStr), parseDt(planArriveStr)
                );
                inserted++;
            } catch (Exception e) {
                errors++;
                if (errors <= 5) log.warn("[阶段1] 异常 fide={}: {}", fide, e.getMessage());
            }

            if ((i + 1) % 500 == 0 || i == allFides.size() - 1) {
                log.info("[阶段1] 进度: {}/{} | 插入={} 跳过={} 错误={}",
                    i + 1, allFides.size(), inserted, skipped, errors);
            }
        }
        log.info("[阶段1] 完成: 插入={}, 跳过={}, 错误={}", inserted, skipped, errors);
    }

    /** 从 FIDE 批量补全 flight_no、airline、plan_depart_time */
    private void phase1bFixFromFide() {
        log.info("[阶段1b] 从 FIDE 批量补全基础字段...");

        int n1 = jdbc.update("""
            UPDATE flight_master SET flight_no = SUBSTRING_INDEX(SUBSTRING_INDEX(fide,'-',2),'-',-1)
            WHERE flight_no IS NULL AND fide LIKE '%-%-%'""");
        log.info("[阶段1b] 补全 flight_no: {} 条", n1);

        int n2 = jdbc.update("""
            UPDATE flight_master SET airline = SUBSTRING_INDEX(fide,'-',1)
            WHERE airline IS NULL AND fide LIKE '%-%-%'""");
        log.info("[阶段1b] 补全 airline: {} 条", n2);

        int n3 = jdbc.update("""
            UPDATE flight_master SET plan_depart_time = STR_TO_DATE(
              SUBSTRING_INDEX(SUBSTRING_INDEX(fide,'-',3),'-',-1), '%Y%m%d%H%i%s')
            WHERE (plan_depart_time IS NULL OR plan_depart_time = '0000-00-00 00:00:00')
            AND fide LIKE '%-%-%-%'""");
        log.info("[阶段1b] 补全 plan_depart_time: {} 条", n3);
    }

    // ==================== 阶段2：DFME 事件更新状态 ====================

    private void phase2UpdateStatus() {
        log.info("[阶段2] 从 dfme_message 更新状态/时间...");
        String[] styps = {"FPTT","DEPE","ARRE","DLYE","CANE","RTNE","ONRE","FRTT","FETT","AIRL","BORE","POKE","CKIE","CKOE"};
        for (String styp : styps) {
            int total = countDfme(styp);
            if (total == 0) { log.info("[阶段2] {} 无数据，跳过", styp); continue; }
            log.info("[阶段2] {}: {} 条...", styp, total);

            int updated = 0, errors = 0;
            for (int offset = 0; offset < total; offset += BATCH) {
                List<Map<String, Object>> rows = queryDfme(styp, offset, BATCH);
                for (Map<String, Object> row : rows) {
                    try {
                        String raw = rawToString(row.get("raw_data"));
                        if (raw == null) continue;
                        JsonNode body = mapper.readTree(raw);
                        String fide = text(body, "FIDE");
                        if (fide == null) continue;

                        switch (styp) {
                            case "FPTT" -> updatePlanTime(fide, text(body, "FPTT"), text(body, "FPLT"));
                            case "DEPE" -> updateStatus(fide, "DEP", "已起飞", text(body, "FRTT"), false);
                            case "ARRE" -> updateStatus(fide, "ARR", "已到达", text(body, "FRLT"), true);
                            case "DLYE" -> updateStatus(fide, "DLY", "延误", null, false);
                            case "CANE" -> updateStatus(fide, "CAN", "取消", null, false);
                            case "RTNE" -> updateStatus(fide, "RTN", "返航", null, false);
                            case "ONRE" -> updateStatus(fide, "DEP", "已起飞", text(body, "PAST"), false);
                            case "FRTT" -> updateTime(fide, text(body, "FRTT"), text(body, "FRLT"));
                            case "FETT" -> updateTime(fide, text(body, "FETT"), text(body, "FELT"));
                            case "AIRL" -> updateDestination(fide, body);
                            case "BORE","POKE","CKIE","CKOE" -> {} // 不更新核心字段
                        }
                        updated++;
                    } catch (Exception e) { errors++; }
                }
            }
            log.info("[阶段2] {} 完成: 更新={}, 错误={}", styp, updated, errors);
        }
    }

    // ==================== 更新方法 ====================

    private void updatePlanTime(String fide, String fptt, String fplt) {
        jdbc.update("UPDATE flight_master SET plan_depart_time=?, plan_arrive_time=?, update_time=NOW() WHERE fide=?",
            parseDt(fptt), parseDt(fplt), fide);
    }

    private void updateTime(String fide, String time1, String time2) {
        if (time1 != null) jdbc.update("UPDATE flight_master SET plan_depart_time=?, update_time=NOW() WHERE fide=?",
            parseDt(time1), fide);
        if (time2 != null) jdbc.update("UPDATE flight_master SET plan_arrive_time=?, update_time=NOW() WHERE fide=?",
            parseDt(time2), fide);
    }

    private void updateStatus(String fide, String code, String name, String actualTime, boolean isArrive) {
        String sql = "UPDATE flight_master SET status_code=?, status_name=?, update_time=NOW()";
        if (actualTime != null && isArrive) sql += ", actual_arrive_time=?";
        if (actualTime != null && !isArrive) sql += ", actual_depart_time=?";
        sql += " WHERE fide=? AND (status_code IS NULL OR status_code NOT IN('CAN','RTN') OR ? IN('CAN','RTN'))";

        List<Object> params = new ArrayList<>(List.of(code, name));
        if (actualTime != null) params.add(parseDt(actualTime));
        params.add(fide);
        params.add(code);

        jdbc.update(sql, params.toArray());
    }

    private void updateDestination(String fide, JsonNode body) {
        JsonNode arpts = body.path("AIRL").path("ARPT");
        if (arpts.isArray() && arpts.size() > 0) {
            JsonNode last = arpts.get(arpts.size() - 1);
            String dc = text(last, "APCD");
            if (dc != null) jdbc.update(
                "UPDATE flight_master SET destination_code=?, destination=?, update_time=NOW() WHERE fide=?", dc, dc, fide);
        }
    }

    /** 优先取核心事件（有 FLNO/AWCD），资源分配事件排在后面 */
    private Map<String, Object> queryBestEvent(String fide) {
        // 优先级：核心航班事件 > 资源分配事件
        final String SQL = """
            SELECT CAST(raw_data AS CHAR) AS raw_data FROM (
              SELECT raw_data,
                CASE WHEN styp IN('DEPE','ARRE','FPTT','ONRE','DLYE','CANE','RTNE',
                                  'BORE','POKE','CKIE','CKOE','FETT','FRTT','CFCE','HBTT','AIRL','DFUE')
                     THEN 1 ELSE 2 END AS prio
              FROM dfme_message WHERE fide = ?
              ORDER BY prio, ddtm, id LIMIT 1
            ) t""";
        return jdbc.queryForMap(SQL, fide);
    }

    // ==================== 辅助 ====================

    private int countDfme(String styp) {
        Integer c = jdbc.queryForObject("SELECT COUNT(*) FROM dfme_message WHERE styp=?", Integer.class, styp);
        return c != null ? c : 0;
    }

    private List<Map<String, Object>> queryDfme(String styp, int offset, int limit) {
        return jdbc.queryForList(
            "SELECT CAST(raw_data AS CHAR) AS raw_data FROM dfme_message WHERE styp=? LIMIT ?,?", styp, offset, limit);
    }

    private String extractTimeFromFide(String fide) {
        String[] parts = fide.split("-");
        if (parts.length >= 3) {
            String t = parts[parts.length - 2];
            if (t.length() >= 14) return t;
        }
        return null;
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

    private String or(String val, String fallback) { return val != null ? val : fallback; }

    private LocalDateTime parseDt(String dt) {
        if (dt == null || dt.length() < 14) return null;
        try {
            return LocalDateTime.of(
                Integer.parseInt(dt.substring(0,4)), Integer.parseInt(dt.substring(4,6)),
                Integer.parseInt(dt.substring(6,8)), Integer.parseInt(dt.substring(8,10)),
                Integer.parseInt(dt.substring(10,12)), Integer.parseInt(dt.substring(12,14)));
        } catch (Exception e) { return null; }
    }
}
