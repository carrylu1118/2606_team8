package com.binair.admin.service;

import com.binair.admin.entity.FlightMaster;
import com.binair.admin.entity.MetaEntity;
import com.binair.admin.entity.xml.Meta;
import com.binair.admin.mapper.BaseBusinessMapper;
import com.binair.admin.mapper.DfmeBusinessMapper;
import com.binair.admin.mapper.DfoeBusinessMapper;
import com.binair.admin.mapper.MetaMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 消息路由器 — 根据 TYPE + STYP 将 XML 消息写入对应的业务表
 * <p>
 * 流程：TB_META → 解析 bodyJson → 按 TYPE 分发 → 调用对应 Mapper
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageRouterService {

    private final MetaMapper metaMapper;
    private final BaseBusinessMapper baseMapper;
    private final DfmeBusinessMapper dfmeMapper;
    private final DfoeBusinessMapper dfoeMapper;
    private final FlightMasterService flightMasterService;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 入口：接收 Meta + bodyJson，写入对应业务表
     */
    public void route(Meta meta, String bodyJson) {
        // 1. 插入 TB_META
        MetaEntity metaEntity = new MetaEntity();
        metaEntity.setSndr(meta.getSndr());
        metaEntity.setRcvr(meta.getRcvr());
        metaEntity.setSeqn(meta.getSeqn());
        metaEntity.setDdtm(meta.getDdtm());
        metaEntity.setType(meta.getType());
        metaEntity.setStyp(meta.getStyp());
        metaEntity.setCreateTime(LocalDateTime.now());
        metaMapper.insert(metaEntity);
        Long metaId = metaEntity.getId();

        // 2. 解析 body
        JsonNode body = null;
        if (bodyJson != null) {
            try {
                body = objectMapper.readTree(bodyJson);
            } catch (Exception e) {
                log.warn("解析 bodyJson 失败: {}", e.getMessage());
            }
        }

        // 3. 按 TYPE 分发
        String type = meta.getType();
        String styp = meta.getStyp();
        try {
            if ("BASE".equalsIgnoreCase(type)) {
                routeBase(styp, body, metaId);
            } else if ("DFOE".equalsIgnoreCase(type)) {
                routeDfoe(styp, body, metaId);
            } else if ("DFME".equalsIgnoreCase(type)) {
                routeDfme(styp, body, metaId);
            } else {
                log.warn("未知消息类型 TYPE={}", type);
            }
        } catch (Exception e) {
            log.error("消息入库失败 TYPE={} STYP={}: {}", type, styp, e.getMessage(), e);
            throw e;
        }
    }

    // ==================== BASE 路由 ====================

    private void routeBase(String styp, JsonNode body, Long metaId) {
        Map<String, Object> p = new HashMap<>();
        p.put("metaId", metaId);
        if (body != null) {
            p.put("code", text(body, "CODE"));
            p.put("frcd", text(body, "FRCD"));
            p.put("apat", text(body, "APAT"));
            p.put("cnnm", text(body, "CNNM"));
            p.put("ennm", text(body, "ENNM"));
            p.put("aiso", text(body, "AISO"));
            p.put("apsn", text(body, "APSN"));
            p.put("cftp", text(body, "CFTP"));
            p.put("awcd", text(body, "AWCD"));
            p.put("stnm", parseInt(body, "STNM"));
            p.put("rstn", parseInt(body, "RSTN"));
        }

        switch (styp.toUpperCase()) {
            case "APUE" -> baseMapper.insertApue(p);
            case "CFIE" -> baseMapper.insertCfie(p);
            case "CFUE" -> baseMapper.insertCfue(p);
            default -> log.warn("未知 BASE 子类型: {}", styp);
        }
    }

    // ==================== DFOE 路由 ====================

    private void routeDfoe(String styp, JsonNode body, Long metaId) {
        Map<String, Object> p = new HashMap<>();
        p.put("metaId", metaId);
        if (body != null) {
            p.put("flid", text(body, "FLID"));
            p.put("ffid", text(body, "FFID"));
            p.put("fide", text(body, "FIDE"));
            p.put("awcd", text(body, "AWCD"));
            p.put("flno", text(body, "FLNO"));
            p.put("fexd", text(body, "FEXD"));
            p.put("flio", text(body, "FLIO"));
            p.put("fltk", text(body, "FLTK"));
            p.put("fatt", text(body, "FATT"));
            p.put("cftp", text(body, "CFTP"));
            p.put("cfno", text(body, "CFNO"));
            p.put("dltp", text(body, "DLTP"));
            p.put("recd", parseInt(body, "RECD"));
        }

        switch (styp.toUpperCase()) {
            case "DFIE" -> {
                dfoeMapper.insertDfie(p);
                initFlightFromDfie(body);
            }
            case "DFDE" -> dfoeMapper.insertDfde(p);
            case "DFDL" -> dfoeMapper.insertDfdl(p);
            default -> log.warn("未知 DFOE 子类型: {}", styp);
        }
    }

    // ==================== DFME 路由 ====================

    private void routeDfme(String styp, JsonNode body, Long metaId) {
        if (body == null) {
            log.warn("DFME body 为空: styp={}", styp);
            return;
        }

        switch (styp.toUpperCase()) {
            // 单表（无明细）
            case "AFID" -> dfmeMapper.insertAfid(dfmeParams(metaId, body));
            case "ARRE" -> {
                dfmeMapper.insertArre(dfmeParams(metaId, body));
                updateFlightStatus(body, "ARR", "已到达", text(body, "FRLT"));
            }
            case "BORE" -> dfmeMapper.insertBore(dfmeParams(metaId, body));
            case "CANE" -> {
                dfmeMapper.insertCane(dfmeParams(metaId, body));
                updateFlightStatus(body, "CAN", "取消", null);
            }
            case "CFCE" -> dfmeMapper.insertCfce(dfmeParams(metaId, body));
            case "CKIE" -> dfmeMapper.insertCkie(dfmeParams(metaId, body));
            case "CKOE" -> dfmeMapper.insertCkoe(dfmeParams(metaId, body));
            case "DEPE" -> {
                dfmeMapper.insertDepe(dfmeParams(metaId, body));
                updateFlightStatus(body, "DEP", "已起飞", text(body, "FRTT"));
            }
            case "DLYE" -> {
                dfmeMapper.insertDlye(dfmeParams(metaId, body));
                updateFlightStatus(body, "DLY", "延误", null);
            }
            case "FETT" -> dfmeMapper.insertFett(dfmeParams(metaId, body));
            case "FPTT" -> {
                dfmeMapper.insertFptt(dfmeParams(metaId, body));
                updateFlightPlanTime(body);
            }
            case "FRTT" -> dfmeMapper.insertFrtt(dfmeParams(metaId, body));
            case "HBTT" -> dfmeMapper.insertHbtt(dfmeParams(metaId, body));
            case "LBDE" -> dfmeMapper.insertLbde(dfmeParams(metaId, body));
            case "ONRE" -> {
                dfmeMapper.insertOnre(dfmeParams(metaId, body));
                updateFlightStatus(body, "DEP", "已起飞", text(body, "PAST"));
            }
            case "POKE" -> dfmeMapper.insertPoke(dfmeParams(metaId, body));
            case "RTNE" -> {
                dfmeMapper.insertRtne(dfmeParams(metaId, body));
                updateFlightStatus(body, "RTN", "返航", null);
            }

            // 航站信息（DFME-APRTINFO 可能独立出现）
            case "APRTINFO" -> dfmeMapper.insertAprtinfo(dfmeParams(metaId, body));

            // 主表+明细
            case "AIRL" -> routeAirl(body, metaId);
            case "BLLS" -> routeBlls(body, metaId);
            case "CKLS" -> routeCkls(body, metaId);
            case "GTLS" -> routeGtls(body, metaId);
            case "STLS" -> routeStls(body, metaId);
            case "SFLG" -> routeSflg(body, metaId);
            case "DFUE" -> routeDfue(body, metaId);

            default -> log.warn("未知 DFME 子类型: {}", styp);
        }
    }

    // ==================== 主表+明细 处理方法 ====================

    /** AIRL：航线变更 → 主表 + 航站列表 → 更新 flight_master 目的地 */
    private void routeAirl(JsonNode body, Long metaId) {
        Map<String, Object> main = dfmeMainParams(metaId, body);
        dfmeMapper.insertAirlMain(main);
        Long masterId = generatedId(main);

        JsonNode arpts = body.path("AIRL").path("ARPT");
        if (arpts.isArray()) {
            // 最后一个航站是目的地
            JsonNode lastArpt = arpts.get(arpts.size() - 1);
            for (JsonNode arpt : arpts) {
                Map<String, Object> d = new HashMap<>();
                d.put("masterId", masterId);
                d.put("apno", parseInt(arpt, "APNO"));
                d.put("apcd", text(arpt, "APCD"));
                d.put("fptt", text(arpt, "FPTT"));
                d.put("fplt", text(arpt, "FPLT"));
                d.put("apat", text(arpt, "APAT"));
                dfmeMapper.insertAirlDetail(d);
            }
            // 更新航班主表目的地
            String destCode = text(lastArpt, "APCD");
            if (destCode != null) {
                flightMasterService.updateDestinationByFide(
                        text(body, "FIDE"), destCode, destCode);
            }
        }
    }

    /** BLLS：行李转盘 → 主表 + 转盘列表 */
    private void routeBlls(JsonNode body, Long metaId) {
        Map<String, Object> main = dfmeMainParams(metaId, body);
        main.put("awcd", text(body, "AWCD"));
        main.put("flno", text(body, "FLNO"));
        dfmeMapper.insertBllsMain(main);
        Long masterId = generatedId(main);

        JsonNode belts = body.path("BLLS").path("BELT");
        if (belts.isArray()) {
            for (JsonNode belt : belts) {
                Map<String, Object> d = new HashMap<>();
                d.put("masterId", masterId);
                d.put("btno", parseInt(belt, "BTNO"));
                d.put("code", text(belt, "CODE"));
                d.put("btat", text(belt, "BTAT"));
                d.put("estr", text(belt, "ESTR"));
                d.put("eend", text(belt, "EEND"));
                d.put("rstr", text(belt, "RSTR"));
                d.put("rend", text(belt, "REND"));
                d.put("btsc", text(belt, "BTSC"));
                d.put("exno", text(belt, "EXNO"));
                dfmeMapper.insertBllsDetail(d);
            }
        }
    }

    /** CKLS：值机柜台 → 主表 + 柜台列表 */
    private void routeCkls(JsonNode body, Long metaId) {
        Map<String, Object> main = dfmeMainParams(metaId, body);
        JsonNode ckls = body.path("CKLS");
        main.put("fces", text(ckls, "FCES"));
        main.put("fcee", text(ckls, "FCEE"));
        main.put("fcrs", text(ckls, "FCRS"));
        main.put("fcre", text(ckls, "FCRE"));
        main.put("mces", text(ckls, "MCES"));
        main.put("mcee", text(ckls, "MCEE"));
        main.put("mcrs", text(ckls, "MCRS"));
        main.put("mcre", text(ckls, "MCRE"));
        main.put("fcdp", text(ckls, "FCDP"));
        main.put("mcdp", text(ckls, "MCDP"));
        dfmeMapper.insertCklsMain(main);
        Long masterId = generatedId(main);

        // 柜台列表（COUNTER）
        JsonNode counters = ckls.path("COUNTER");
        if (counters.isArray()) {
            for (JsonNode c : counters) {
                Map<String, Object> d = counterParams(c, masterId);
                dfmeMapper.insertCklsCntr(d);
            }
        }
        // 删除柜台列表（DELTCK）
        JsonNode deltcks = ckls.path("DELTCK");
        if (deltcks.isArray()) {
            for (JsonNode c : deltcks) {
                Map<String, Object> d = counterParams(c, masterId);
                dfmeMapper.insertCklsDeltck(d);
            }
        }
    }

    /** GTLS：登机门 → 主表 + 登机门列表 */
    private void routeGtls(JsonNode body, Long metaId) {
        Map<String, Object> main = dfmeMainParams(metaId, body);
        dfmeMapper.insertGtlsMain(main);
        Long masterId = generatedId(main);

        JsonNode gates = body.path("GTLS").path("GATE");
        if (gates.isArray()) {
            for (JsonNode gate : gates) {
                Map<String, Object> d = new HashMap<>();
                d.put("masterId", masterId);
                d.put("gtno", parseInt(gate, "GTNO"));
                d.put("code", text(gate, "CODE"));
                d.put("gtat", text(gate, "GTAT"));
                d.put("estr", text(gate, "ESTR"));
                d.put("eend", text(gate, "EEND"));
                d.put("rstr", text(gate, "RSTR"));
                d.put("rend", text(gate, "REND"));
                d.put("btsc", text(gate, "BTSC"));
                dfmeMapper.insertGtlsGate(d);
            }
        }
    }

    /** STLS：机位 → 主表 + 机位列表 */
    private void routeStls(JsonNode body, Long metaId) {
        Map<String, Object> main = dfmeMainParams(metaId, body);
        dfmeMapper.insertStlsMain(main);
        Long masterId = generatedId(main);

        JsonNode stands = body.path("STLS").path("STAND");
        if (stands.isArray()) {
            for (JsonNode stand : stands) {
                Map<String, Object> d = new HashMap<>();
                d.put("masterId", masterId);
                d.put("stno", parseInt(stand, "STNO"));
                d.put("code", text(stand, "CODE"));
                d.put("estr", text(stand, "ESTR"));
                d.put("eend", text(stand, "EEND"));
                d.put("rstr", text(stand, "RSTR"));
                d.put("rend", text(stand, "REND"));
                d.put("btsc", text(stand, "BTSC"));
                d.put("cssi", text(stand, "CSSI"));
                dfmeMapper.insertStlsInfo(d);
            }
        }
    }

    /** SFLG：共享航班 → 主表 + 共享列表 */
    private void routeSflg(JsonNode body, Long metaId) {
        Map<String, Object> main = dfmeMainParams(metaId, body);
        dfmeMapper.insertSflgMain(main);
        Long masterId = generatedId(main);

        JsonNode sflgs = body.path("SFLG").path("SFLGINFO");
        if (sflgs.isArray()) {
            for (JsonNode sf : sflgs) {
                Map<String, Object> d = new HashMap<>();
                d.put("masterId", masterId);
                d.put("sfaw", text(sf, "SFAW"));
                d.put("sfno", text(sf, "SFNO"));
                dfmeMapper.insertSflgInfo(d);
            }
        }
    }

    /** DFUE：航班更新 → 主表 + 航线/值机/航站楼 三个明细 */
    private void routeDfue(JsonNode body, Long metaId) {
        Map<String, Object> main = dfmeMainParams(metaId, body);
        dfmeMapper.insertDfueMain(main);
        Long masterId = generatedId(main);

        // 航线更新
        JsonNode arpts = body.path("AIRL").path("ARPT");
        if (arpts.isArray()) {
            for (JsonNode arpt : arpts) {
                Map<String, Object> d = new HashMap<>();
                d.put("masterId", masterId);
                d.put("apno", parseInt(arpt, "APNO"));
                d.put("apcd", text(arpt, "APCD"));
                d.put("fptt", text(arpt, "FPTT"));
                d.put("fplt", text(arpt, "FPLT"));
                dfmeMapper.insertDfueAirl(d);
            }
        }

        // 值机更新
        JsonNode ckls = body.path("CKLS");
        if (!ckls.isMissingNode()) {
            Map<String, Object> d = new HashMap<>();
            d.put("masterId", masterId);
            d.put("fces", text(ckls, "FCES"));
            d.put("fcee", text(ckls, "FCEE"));
            d.put("fcrs", text(ckls, "FCRS"));
            d.put("fcre", text(ckls, "FCRE"));
            d.put("mces", text(ckls, "MCES"));
            d.put("mcee", text(ckls, "MCEE"));
            d.put("mcrs", text(ckls, "MCRS"));
            d.put("mcre", text(ckls, "MCRE"));
            dfmeMapper.insertDfueCkls(d);
        }

        // 航站楼更新
        JsonNode tmcd = body.path("TMCD");
        if (!tmcd.isMissingNode()) {
            Map<String, Object> d = new HashMap<>();
            d.put("masterId", masterId);
            d.put("nmcd", text(tmcd, "NMCD"));
            d.put("jmcd", text(tmcd, "JMCD"));
            dfmeMapper.insertDfueTmcd(d);
        }
    }

    // ==================== 参数构建辅助方法 ====================

    /** 构建 DFME 通用字段 Map（含 metaId + flid/ffid/fide 等） */
    private Map<String, Object> dfmeParams(Long metaId, JsonNode body) {
        Map<String, Object> p = dfmeMainParams(metaId, body);
        p.put("awcd", text(body, "AWCD"));
        p.put("flno", text(body, "FLNO"));
        p.put("flio", text(body, "FLIO"));
        p.put("ista", text(body, "ISTA"));
        p.put("abst", text(body, "ABST"));
        p.put("iast", text(body, "IAST"));
        p.put("abrs", text(body, "ABRS"));
        // 时间相关
        p.put("frlt", text(body, "FRLT"));
        p.put("frtt", text(body, "FRTT"));
        p.put("bort", text(body, "BORT"));
        p.put("mbor", text(body, "MBOR"));
        p.put("fett", text(body, "FETT"));
        p.put("felt", text(body, "FELT"));
        p.put("fptt", text(body, "FPTT"));
        p.put("fplt", text(body, "FPLT"));
        p.put("fcrs", text(body, "FCRS"));
        p.put("fcre", text(body, "FCRE"));
        p.put("past", text(body, "PAST"));
        p.put("eldt", text(body, "ELDT"));
        p.put("pokt", text(body, "POKT"));
        p.put("mpok", text(body, "MPOK"));
        p.put("lbdt", text(body, "LBDT"));
        p.put("mlbd", text(body, "MLBD"));
        // 飞机相关
        p.put("cftp", text(body, "CFTP"));
        p.put("cfno", text(body, "CFNO"));
        // 衔接
        p.put("afid", text(body, "AFID"));
        // HBTT 特有
        p.put("hbid", text(body, "HBID"));
        p.put("hbie", text(body, "HBIE"));
        p.put("nfln", text(body, "NFLN"));
        p.put("nawc", text(body, "NAWC"));
        // APRTINFO
        p.put("apno", parseInt(body, "APNO"));
        p.put("apcd", text(body, "APCD"));
        p.put("apat", text(body, "APAT"));
        return p;
    }

    /** 构建主表通用字段（metaId + flid/ffid/fide） */
    private Map<String, Object> dfmeMainParams(Long metaId, JsonNode body) {
        Map<String, Object> p = new HashMap<>();
        p.put("metaId", metaId);
        p.put("flid", text(body, "FLID"));
        p.put("ffid", text(body, "FFID"));
        p.put("fide", text(body, "FIDE"));
        p.put("awcd", text(body, "AWCD"));
        p.put("flno", text(body, "FLNO"));
        p.put("flio", text(body, "FLIO"));
        return p;
    }

    /** 柜台公共字段提取 */
    private Map<String, Object> counterParams(JsonNode c, Long masterId) {
        Map<String, Object> d = new HashMap<>();
        d.put("masterId", masterId);
        d.put("ckno", parseInt(c, "CKNO"));
        d.put("code", text(c, "CODE"));
        d.put("ckat", text(c, "CKAT"));
        d.put("type", text(c, "TYPE"));
        d.put("ccar", text(c, "CCAR"));
        d.put("estr", text(c, "ESTR"));
        d.put("eend", text(c, "EEND"));
        d.put("rstr", text(c, "RSTR"));
        d.put("rend", text(c, "REND"));
        d.put("btsc", text(c, "BTSC"));
        return d;
    }

    // ==================== 自增 ID 安全转换 ====================

    /**
     * 从 Map 中安全提取自增 ID（兼容 MySQL JDBC 返回 BigInteger）
     */
    private Long generatedId(Map<String, Object> params) {
        Object idObj = params.get("id");
        if (idObj == null) return null;
        if (idObj instanceof Long) return (Long) idObj;
        if (idObj instanceof Number) return ((Number) idObj).longValue();
        return Long.parseLong(idObj.toString());
    }

    // ==================== JSON 读取辅助 ====================

    private String text(JsonNode node, String fieldName) {
        JsonNode field = node.get(fieldName);
        return (field != null && !field.isNull()) ? field.asText() : null;
    }

    private Integer parseInt(JsonNode node, String fieldName) {
        JsonNode field = node.get(fieldName);
        if (field != null && !field.isNull()) {
            try {
                return field.asInt();
            } catch (Exception e) {
                // 尝试解析字符串
                try {
                    return Integer.parseInt(field.asText().trim());
                } catch (Exception ignored) {
                }
            }
        }
        return null;
    }

    // ==================== FlightMaster 更新 ====================

    /** 从 DFIE 事件初始化航班主表 */
    private void initFlightFromDfie(JsonNode body) {
        if (body == null) return;
        String fide = text(body, "FIDE");
        if (fide == null) return;

        FlightMaster master = new FlightMaster();
        master.setFide(fide);
        master.setFlid(text(body, "FLID"));
        master.setFlightNo(text(body, "FLNO"));
        master.setAirline(text(body, "AWCD"));
        master.setDeparture("天津");
        master.setStatusCode("PLAN");
        master.setStatusName("计划中");

        // 从 AIRL 提取航站信息
        JsonNode arpts = body.path("AIRL").path("ARPT");
        if (arpts.isArray() && arpts.size() > 0) {
            // 第一个航站 = 出发地
            JsonNode first = arpts.get(0);
            String firstCode = text(first, "APCD");
            if (firstCode != null) master.setDeparture(firstCode);
            String fptt = text(first, "FPTT");
            if (fptt != null) master.setPlanDepartTime(parseDateTime(fptt));

            // 最后一个航站 = 目的地
            JsonNode last = arpts.get(arpts.size() - 1);
            String lastCode = text(last, "APCD");
            if (lastCode != null) {
                master.setDestinationCode(lastCode);
                master.setDestination(lastCode);
            }
            String fplt = text(last, "FPLT");
            if (fplt != null) master.setPlanArriveTime(parseDateTime(fplt));
        }

        flightMasterService.updateOrInsert(master);
    }

    /** 更新航班状态（带防降级） */
    private void updateFlightStatus(JsonNode body, String statusCode, String statusName, String actualTimeStr) {
        String fide = text(body, "FIDE");
        if (fide == null) return;

        LocalDateTime actualTime = parseDateTime(actualTimeStr);
        flightMasterService.updateStatusByFide(fide, statusCode, statusName, actualTime);
    }

    /** 更新航班计划时间 */
    private void updateFlightPlanTime(JsonNode body) {
        String fide = text(body, "FIDE");
        if (fide == null) return;

        LocalDateTime planDepart = parseDateTime(text(body, "FPTT"));
        LocalDateTime planArrive = parseDateTime(text(body, "FPLT"));
        flightMasterService.updatePlanTimeByFide(fide, planDepart, planArrive);
    }

    /**
     * 解析日期时间字符串 (yyyyMMddHHmmss) 为 LocalDateTime
     */
    private LocalDateTime parseDateTime(String dt) {
        if (dt == null || dt.length() < 14) return null;
        try {
            int y = Integer.parseInt(dt.substring(0, 4));
            int mo = Integer.parseInt(dt.substring(4, 6));
            int d = Integer.parseInt(dt.substring(6, 8));
            int h = Integer.parseInt(dt.substring(8, 10));
            int mi = Integer.parseInt(dt.substring(10, 12));
            int s = Integer.parseInt(dt.substring(12, 14));
            return LocalDateTime.of(y, mo, d, h, mi, s);
        } catch (Exception e) {
            return null;
        }
    }
}
