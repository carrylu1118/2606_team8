package com.binair.admin.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * BASE 类消息业务 Mapper — 对应 APUE_APOT / CFIE_CRFT / CFUE_CRFT
 */
@Mapper
public interface BaseBusinessMapper {

    /** APUE：机场信息 */
    @Insert("INSERT INTO APUE_APOT (meta_id, code, frcd, apat, cnnm, ennm, aiso, apsn, create_time) " +
            "VALUES (#{metaId}, #{code}, #{frcd}, #{apat}, #{cnnm}, #{ennm}, #{aiso}, #{apsn}, NOW())")
    int insertApue(Map<String, Object> params);

    /** CFIE：飞机增加 */
    @Insert("INSERT INTO CFIE_CRFT (meta_id, code, cftp, awcd, stnm, rstn, create_time) " +
            "VALUES (#{metaId}, #{code}, #{cftp}, #{awcd}, #{stnm}, #{rstn}, NOW())")
    int insertCfie(Map<String, Object> params);

    /** CFUE：飞机变更 */
    @Insert("INSERT INTO CFUE_CRFT (meta_id, code, cftp, awcd, stnm, rstn, create_time) " +
            "VALUES (#{metaId}, #{code}, #{cftp}, #{awcd}, #{stnm}, #{rstn}, NOW())")
    int insertCfue(Map<String, Object> params);
}
