package com.binair.admin.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 航班动态 VO — 面向旅客展示
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightDynamicVO {
    private String planDepartTime;   // 计划起飞时间 "HH:mm"
    private String planDepartFull;   // 计划起飞完整时间 "yyyy-MM-dd HH:mm:ss"
    private String flightNo;          // 航班号（原始如ICU101）
    private String airline;           // 航空公司二字码
    private String airlineName;       // 航空公司中文全称
    private String destination;       // 目的地
    private String destinationCode;   // 目的地三字码
    private String checkCounter;      // 值机柜台
    private String gate;              // 登机口
    private String statusCode;        // 状态码
    private String statusName;        // 状态中文
}
