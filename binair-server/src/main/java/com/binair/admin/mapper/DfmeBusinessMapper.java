package com.binair.admin.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

import java.util.Map;

/**
 * DFME 类消息业务 Mapper — 30+ 张 DFME 表，全部用 @Insert SQL
 */
@Mapper
public interface DfmeBusinessMapper {

    // ==================== DFME 单表（无明细） ====================

    /** AFID：航班衔接变更 */
    @Insert("INSERT INTO AFID_DFLT (meta_id, flid, ffid, fide, afid, create_time) " +
            "VALUES (#{metaId}, #{flid}, #{ffid}, #{fide}, #{afid}, NOW())")
    int insertAfid(Map<String, Object> params);

    /** ARRE：到达本站 */
    @Insert("INSERT INTO ARRE_DFLT (meta_id, flid, ffid, fide, awcd, flno, flio, frlt, ista, create_time) " +
            "VALUES (#{metaId}, #{flid}, #{ffid}, #{fide}, #{awcd}, #{flno}, #{flio}, #{frlt}, #{ista}, NOW())")
    int insertArre(Map<String, Object> params);

    /** BORE：开始登机 */
    @Insert("INSERT INTO BORE_DFLT (meta_id, flid, ffid, fide, awcd, flno, bort, mbor, ista, create_time) " +
            "VALUES (#{metaId}, #{flid}, #{ffid}, #{fide}, #{awcd}, #{flno}, #{bort}, #{mbor}, #{ista}, NOW())")
    int insertBore(Map<String, Object> params);

    /** CANE：取消 */
    @Insert("INSERT INTO CANE_DFLT (meta_id, flid, ffid, fide, awcd, flno, abst, iast, abrs, create_time) " +
            "VALUES (#{metaId}, #{flid}, #{ffid}, #{fide}, #{awcd}, #{flno}, #{abst}, #{iast}, #{abrs}, NOW())")
    int insertCane(Map<String, Object> params);

    /** CFCE：更换飞机 */
    @Insert("INSERT INTO CFCE_DFLT (meta_id, flid, ffid, fide, cftp, cfno, create_time) " +
            "VALUES (#{metaId}, #{flid}, #{ffid}, #{fide}, #{cftp}, #{cfno}, NOW())")
    int insertCfce(Map<String, Object> params);

    /** CKIE：开始值机 */
    @Insert("INSERT INTO CKIE_DELT (meta_id, flid, ffid, fide, awcd, flno, fcrs, ista, create_time) " +
            "VALUES (#{metaId}, #{flid}, #{ffid}, #{fide}, #{awcd}, #{flno}, #{fcrs}, #{ista}, NOW())")
    int insertCkie(Map<String, Object> params);

    /** CKOE：截止值机 */
    @Insert("INSERT INTO CKOE_DELT (meta_id, flid, ffid, fide, awcd, flno, fcre, ista, create_time) " +
            "VALUES (#{metaId}, #{flid}, #{ffid}, #{fide}, #{awcd}, #{flno}, #{fcre}, #{ista}, NOW())")
    int insertCkoe(Map<String, Object> params);

    /** DEPE：本站起飞 */
    @Insert("INSERT INTO DEPE_DELT (meta_id, flid, ffid, fide, awcd, flno, frtt, ista, create_time) " +
            "VALUES (#{metaId}, #{flid}, #{ffid}, #{fide}, #{awcd}, #{flno}, #{frtt}, #{ista}, NOW())")
    int insertDepe(Map<String, Object> params);

    /** DLYE：延误 */
    @Insert("INSERT INTO DLYE_DELT (meta_id, flid, ffid, fide, awcd, flno, abst, iast, abrs, fett, create_time) " +
            "VALUES (#{metaId}, #{flid}, #{ffid}, #{fide}, #{awcd}, #{flno}, #{abst}, #{iast}, #{abrs}, #{fett}, NOW())")
    int insertDlye(Map<String, Object> params);

    /** FETT：预计时间变更 */
    @Insert("INSERT INTO FETT_DELT (meta_id, flid, ffid, fide, awcd, flno, fett, felt, create_time) " +
            "VALUES (#{metaId}, #{flid}, #{ffid}, #{fide}, #{awcd}, #{flno}, #{fett}, #{felt}, NOW())")
    int insertFett(Map<String, Object> params);

    /** FPTT：计划时间变更 */
    @Insert("INSERT INTO FPTT_DELT (meta_id, flid, ffid, fide, awcd, flno, fptt, fplt, create_time) " +
            "VALUES (#{metaId}, #{flid}, #{ffid}, #{fide}, #{awcd}, #{flno}, #{fptt}, #{fplt}, NOW())")
    int insertFptt(Map<String, Object> params);

    /** FRTT：实际时间变更 */
    @Insert("INSERT INTO FRTT_DELT (meta_id, flid, ffid, fide, awcd, flno, frtt, frlt, create_time) " +
            "VALUES (#{metaId}, #{flid}, #{ffid}, #{fide}, #{awcd}, #{flno}, #{frtt}, #{frlt}, NOW())")
    int insertFrtt(Map<String, Object> params);

    /** HBTT：航班号变更 */
    @Insert("INSERT INTO TB_HBTTBODY (meta_id, flid, ffid, fide, hbid, hbie, nfln, nawc, create_time) " +
            "VALUES (#{metaId}, #{flid}, #{ffid}, #{fide}, #{hbid}, #{hbie}, #{nfln}, #{nawc}, NOW())")
    int insertHbtt(Map<String, Object> params);

    /** LBDE：催促登机 */
    @Insert("INSERT INTO TB_LBDEBODY (meta_id, flid, ffid, fide, awcd, flno, lbdt, mlbd, ista, create_time) " +
            "VALUES (#{metaId}, #{flid}, #{ffid}, #{fide}, #{awcd}, #{flno}, #{lbdt}, #{mlbd}, #{ista}, NOW())")
    int insertLbde(Map<String, Object> params);

    /** ONRE：前站起飞 */
    @Insert("INSERT INTO TB_ONREBODY (meta_id, flid, ffid, fide, awcd, flno, past, eldt, ista, create_time) " +
            "VALUES (#{metaId}, #{flid}, #{ffid}, #{fide}, #{awcd}, #{flno}, #{past}, #{eldt}, #{ista}, NOW())")
    int insertOnre(Map<String, Object> params);

    /** POKE：结束登机 */
    @Insert("INSERT INTO TB_POKEBODY (meta_id, flid, ffid, fide, awcd, flno, pokt, mpok, ista, create_time) " +
            "VALUES (#{metaId}, #{flid}, #{ffid}, #{fide}, #{awcd}, #{flno}, #{pokt}, #{mpok}, #{ista}, NOW())")
    int insertPoke(Map<String, Object> params);

    /** RTNE：返航 */
    @Insert("INSERT INTO TB_RTNEBODY (meta_id, flid, ffid, fide, awcd, flno, abst, iast, abrs, create_time) " +
            "VALUES (#{metaId}, #{flid}, #{ffid}, #{fide}, #{awcd}, #{flno}, #{abst}, #{iast}, #{abrs}, NOW())")
    int insertRtne(Map<String, Object> params);

    /** APRTINFO：航站信息 */
    @Insert("INSERT INTO TB_APRTINFO (meta_id, flid, ffid, fide, apno, apcd, fptt, fplt, apat, create_time) " +
            "VALUES (#{metaId}, #{flid}, #{ffid}, #{fide}, #{apno}, #{apcd}, #{fptt}, #{fplt}, #{apat}, NOW())")
    int insertAprtinfo(Map<String, Object> params);

    // ==================== DFME 主表（useGeneratedKeys 回填 ID） ====================

    /** AIRL：航线变更主表 */
    @Insert("INSERT INTO AIRL_DFLT (meta_id, flid, ffid, fide, awcd, flno, flio, create_time) " +
            "VALUES (#{metaId}, #{flid}, #{ffid}, #{fide}, #{awcd}, #{flno}, #{flio}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertAirlMain(Map<String, Object> params);

    /** BLLS：行李转盘主表 */
    @Insert("INSERT INTO BLLS_DFLT (meta_id, flid, ffid, fide, awcd, flno, create_time) " +
            "VALUES (#{metaId}, #{flid}, #{ffid}, #{fide}, #{awcd}, #{flno}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertBllsMain(Map<String, Object> params);

    /** CKLS：值机柜台主表 */
    @Insert("INSERT INTO CKLS_DELT (meta_id, flid, ffid, fide, awcd, flno, " +
            "fces, fcee, fcrs, fcre, mces, mcee, mcrs, mcre, fcdp, mcdp, create_time) " +
            "VALUES (#{metaId}, #{flid}, #{ffid}, #{fide}, #{awcd}, #{flno}, " +
            "#{fces}, #{fcee}, #{fcrs}, #{fcre}, #{mces}, #{mcee}, #{mcrs}, #{mcre}, #{fcdp}, #{mcdp}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertCklsMain(Map<String, Object> params);

    /** GTLS：登机门主表 */
    @Insert("INSERT INTO GTLS_DELT (meta_id, flid, ffid, fide, awcd, flno, create_time) " +
            "VALUES (#{metaId}, #{flid}, #{ffid}, #{fide}, #{awcd}, #{flno}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertGtlsMain(Map<String, Object> params);

    /** STLS：机位主表 */
    @Insert("INSERT INTO TB_STLSBODY (meta_id, flid, ffid, fide, awcd, flno, create_time) " +
            "VALUES (#{metaId}, #{flid}, #{ffid}, #{fide}, #{awcd}, #{flno}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertStlsMain(Map<String, Object> params);

    /** SFLG：共享航班主表 */
    @Insert("INSERT INTO TB_SFLGBODY (meta_id, flid, ffid, fide, awcd, flno, create_time) " +
            "VALUES (#{metaId}, #{flid}, #{ffid}, #{fide}, #{awcd}, #{flno}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertSflgMain(Map<String, Object> params);

    /** DFUE：航班更新主表 */
    @Insert("INSERT INTO DFUE_DFLT (meta_id, flid, ffid, fide, awcd, flno, create_time) " +
            "VALUES (#{metaId}, #{flid}, #{ffid}, #{fide}, #{awcd}, #{flno}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertDfueMain(Map<String, Object> params);

    // ==================== DFME 明细表 ====================

    /** AIRL 明细：航站列表 */
    @Insert("INSERT INTO AIRL_DFLTDETAIL (master_id, apno, apcd, fptt, fplt, apat, create_time) " +
            "VALUES (#{masterId}, #{apno}, #{apcd}, #{fptt}, #{fplt}, #{apat}, NOW())")
    int insertAirlDetail(Map<String, Object> params);

    /** BLLS 明细：行李转盘 */
    @Insert("INSERT INTO BLLS_DFLTDETAIL (master_id, btno, code, btat, estr, eend, rstr, rend, btsc, exno, create_time) " +
            "VALUES (#{masterId}, #{btno}, #{code}, #{btat}, #{estr}, #{eend}, #{rstr}, #{rend}, #{btsc}, #{exno}, NOW())")
    int insertBllsDetail(Map<String, Object> params);

    /** CKLS 明细：值机柜台 */
    @Insert("INSERT INTO CKLS_CNTR (master_id, ckno, code, ckat, type, ccar, estr, eend, rstr, rend, btsc, create_time) " +
            "VALUES (#{masterId}, #{ckno}, #{code}, #{ckat}, #{type}, #{ccar}, #{estr}, #{eend}, #{rstr}, #{rend}, #{btsc}, NOW())")
    int insertCklsCntr(Map<String, Object> params);

    /** CKLS 明细：删除柜台 */
    @Insert("INSERT INTO CKLS_DELTCK (master_id, ckno, code, ckat, type, ccar, estr, eend, rstr, rend, btsc, create_time) " +
            "VALUES (#{masterId}, #{ckno}, #{code}, #{ckat}, #{type}, #{ccar}, #{estr}, #{eend}, #{rstr}, #{rend}, #{btsc}, NOW())")
    int insertCklsDeltck(Map<String, Object> params);

    /** GTLS 明细：登机门 */
    @Insert("INSERT INTO DELT_GATE (master_id, gtno, code, gtat, estr, eend, rstr, rend, btsc, create_time) " +
            "VALUES (#{masterId}, #{gtno}, #{code}, #{gtat}, #{estr}, #{eend}, #{rstr}, #{rend}, #{btsc}, NOW())")
    int insertGtlsGate(Map<String, Object> params);

    /** STLS 明细：机位 */
    @Insert("INSERT INTO TB_STLSINFO (master_id, stno, code, estr, eend, rstr, rend, btsc, cssi, create_time) " +
            "VALUES (#{masterId}, #{stno}, #{code}, #{estr}, #{eend}, #{rstr}, #{rend}, #{btsc}, #{cssi}, NOW())")
    int insertStlsInfo(Map<String, Object> params);

    /** SFLG 明细：共享航班号 */
    @Insert("INSERT INTO TB_SFLGINFO (master_id, sfaw, sfno, create_time) " +
            "VALUES (#{masterId}, #{sfaw}, #{sfno}, NOW())")
    int insertSflgInfo(Map<String, Object> params);

    /** DFUE 航线更新明细 */
    @Insert("INSERT INTO DFUE_DFLTAIRL (master_id, apno, apcd, fptt, fplt, create_time) " +
            "VALUES (#{masterId}, #{apno}, #{apcd}, #{fptt}, #{fplt}, NOW())")
    int insertDfueAirl(Map<String, Object> params);

    /** DFUE 值机更新明细 */
    @Insert("INSERT INTO DFUE_DFLTCKLS (master_id, fces, fcee, fcrs, fcre, mces, mcee, mcrs, mcre, create_time) " +
            "VALUES (#{masterId}, #{fces}, #{fcee}, #{fcrs}, #{fcre}, #{mces}, #{mcee}, #{mcrs}, #{mcre}, NOW())")
    int insertDfueCkls(Map<String, Object> params);

    /** DFUE 航站楼更新明细 */
    @Insert("INSERT INTO DFUE_DFLTTMCD (master_id, nmcd, jmcd, create_time) " +
            "VALUES (#{masterId}, #{nmcd}, #{jmcd}, NOW())")
    int insertDfueTmcd(Map<String, Object> params);
}
