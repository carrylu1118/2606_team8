package com.binair.admin.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * DFOE 类消息业务 Mapper — 对应 TB_DFIEBODY / TB_DFDEBODY / TB_DFDLBODY
 */
@Mapper
public interface DfoeBusinessMapper {

    /** DFIE：航班增加 */
    @Insert("INSERT INTO TB_DFIEBODY (meta_id, flid, ffid, fide, awcd, flno, fexd, flio, fltk, fatt, cftp, cfno, create_time) " +
            "VALUES (#{metaId}, #{flid}, #{ffid}, #{fide}, #{awcd}, #{flno}, #{fexd}, #{flio}, #{fltk}, #{fatt}, #{cftp}, #{cfno}, NOW())")
    int insertDfie(Map<String, Object> params);

    /** DFDE：航班删除 */
    @Insert("INSERT INTO TB_DFDEBODY (meta_id, flid, ffid, fide, create_time) " +
            "VALUES (#{metaId}, #{flid}, #{ffid}, #{fide}, NOW())")
    int insertDfde(Map<String, Object> params);

    /** DFDL：整表同步 */
    @Insert("INSERT INTO TB_DFDLBODY (meta_id, dltp, recd, create_time) " +
            "VALUES (#{metaId}, #{dltp}, #{recd}, NOW())")
    int insertDfdl(Map<String, Object> params);
}
