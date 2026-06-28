package com.binair.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 机场基础实体 — 映射到 airport_base 表
 */
@Data
@TableName("airport_base")
public class Airport {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /** 机场三字码 */
    private String code;

    /** 机场四字码（ICAO） */
    private String frcd;

    /** 机场属性：2403=国内 */
    private String apat;

    /** 中文名称 */
    private String cnnm;

    /** 英文名称 */
    private String ennm;

    /** 是否开启：O=开启 */
    private String aiso;

    /** 航站简称 */
    private String apsn;

    /** 消息发送者 */
    private String metaSndr;

    /** 消息序号 */
    private String metaSeqn;

    /** 消息时间 */
    private String metaDdtm;

    /** 创建时间 */
    private LocalDateTime createTime;
}
