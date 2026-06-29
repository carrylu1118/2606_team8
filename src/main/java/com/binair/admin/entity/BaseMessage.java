package com.binair.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@TableName("base_message")
public class BaseMessage {

    @TableId(type = IdType.AUTO)
    private Long id;

    // ===== 消息头（所有 BASE 共有） =====
    private String sndr;          // 发送者
    private String rcvr;          // 接收者
    private String seqn;          // 序号
    private String ddtm;          // 消息时间
    private String type;          // 固定为 BASE
    private String styp;          // 子类型：APUE/CFIE/CFUE

    // ===== 机场字段（APUE） =====
    private String code;          // 机场三字码
    private String frcd;          // 机场四字码
    private String apat;          // 机场属性
    private String cnnm;          // 中文名称
    private String ennm;          // 英文名称
    private String aiso;          // 是否开启
    private String apsn;          // 航站简称

    // ===== 飞机字段（CFIE/CFUE） =====
    private String cfCode;        // 飞机编号
    private String cfTp;          // 飞机机型
    private String awcd;          // 所属航空公司
    private String stnm;          // 最大座位数
    private String rstn;          // 可供座位数

    // ===== 整个 XML 存为 JSON =====
    private String rawData;

    // ===== 时间戳 =====
    private LocalDateTime createTime;
}