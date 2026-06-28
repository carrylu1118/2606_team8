package com.binair.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 航班衔接实体 — 映射到 flight_afid 表
 */
@Data
@TableName("flight_afid")
public class FlightAfid {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /** 航班唯一编号 */
    private String flid;

    /** 航班标识（旧格式） */
    private String ffid;

    /** 航班标识（新格式，推荐） */
    private String fide;

    /** 衔接航班ID（空=取消衔接） */
    private String afid;

    /** 消息发送者 */
    private String metaSndr;

    /** 消息序号 */
    private String metaSeqn;

    /** 消息时间 */
    private String metaDdtm;

    /** 创建时间 */
    private LocalDateTime createTime;
}
