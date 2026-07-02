package com.binair.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 航班主表 — 汇总所有航班信息，用于列表查询和状态管理
 */
@Data
@TableName("flight_master")
public class FlightMaster {
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 航班唯一标识 */
    private String fide;

    /** 航班ID */
    private String flid;

    /** 航班号，如 CA1319 */
    private String flightNo;

    /** 航空公司二字码，如 CA */
    private String airline;

    /** 出发地，默认"天津" */
    private String departure;

    /** 目的地中文名 */
    private String destination;

    /** 目的地三字码 */
    private String destinationCode;

    /** 计划离港时间 */
    private LocalDateTime planDepartTime;

    /** 计划到港时间 */
    private LocalDateTime planArriveTime;

    /** 实际离港时间 */
    private LocalDateTime actualDepartTime;

    /** 实际到港时间 */
    private LocalDateTime actualArriveTime;

    /** 状态编码：PLAN/DEP/ARR/DLY/CAN/RTN */
    private String statusCode;

    /** 状态中文：计划中/已起飞/已到达/延误/取消/返航 */
    private String statusName;

    private String checkCounter;  // 值机柜台
    private String gate;          // 登机口

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
