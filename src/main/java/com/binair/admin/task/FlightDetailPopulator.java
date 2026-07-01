package com.binair.admin.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * 一次性任务：从 DELT_GATE / CKLS_CNTR 明细表反填 flight_master 的 gate / check_counter
 * <p>
 * 开关：flight-detail-populate.enabled = true
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "flight-detail-populate.enabled", havingValue = "true")
public class FlightDetailPopulator implements CommandLineRunner {

    private final JdbcTemplate jdbc;

    @Override
    public void run(String... args) {
        log.info("========================================");
        log.info("FlightMaster 值机柜台 / 登机口 填充开始");
        log.info("========================================");

        ensureColumns();
        populateGate();
        populateCheckCounter();

        log.info("========================================");
        log.info("FlightMaster 值机柜台 / 登机口 填充结束");
        log.info("========================================");
    }

    /** 确保 flight_master 有 check_counter 和 gate 列 */
    private void ensureColumns() {
        try {
            jdbc.execute("ALTER TABLE flight_master ADD COLUMN check_counter VARCHAR(50) COMMENT '值机柜台'");
            log.info("已添加列 flight_master.check_counter");
        } catch (Exception e) {
            log.info("列 check_counter 已存在，跳过");
        }
        try {
            jdbc.execute("ALTER TABLE flight_master ADD COLUMN gate VARCHAR(50) COMMENT '登机口'");
            log.info("已添加列 flight_master.gate");
        } catch (Exception e) {
            log.info("列 gate 已存在，跳过");
        }
    }

    /** 从 DELT_GATE 填 gate */
    private void populateGate() {
        log.info("[登机口] 开始填充...");
        int updated = jdbc.update(
            "UPDATE flight_master fm SET fm.gate = (" +
            "  SELECT dg.code FROM gtls_delt gd " +
            "  JOIN delt_gate dg ON dg.master_id = gd.id " +
            "  WHERE gd.fide = fm.fide ORDER BY dg.gtno LIMIT 1" +
            ") WHERE EXISTS (" +
            "  SELECT 1 FROM gtls_delt gd WHERE gd.fide = fm.fide" +
            ")"
        );
        log.info("[登机口] 完成: 更新 {} 条", updated);
    }

    /** 从 CKLS_CNTR 填 check_counter */
    private void populateCheckCounter() {
        log.info("[值机柜台] 开始填充...");
        int updated = jdbc.update(
            "UPDATE flight_master fm SET fm.check_counter = (" +
            "  SELECT cc.code FROM ckls_delt cd " +
            "  JOIN ckls_cntr cc ON cc.master_id = cd.id " +
            "  WHERE cd.fide = fm.fide ORDER BY cc.ckno LIMIT 1" +
            ") WHERE EXISTS (" +
            "  SELECT 1 FROM ckls_delt cd WHERE cd.fide = fm.fide" +
            ")"
        );
        log.info("[值机柜台] 完成: 更新 {} 条", updated);
    }
}
