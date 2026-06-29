package com.binair.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("dfoe_message")
public class DfoeMessage {

    @TableId(type = IdType.AUTO)
    private Long id;

    // ===== 消息头 =====
    private String sndr;
    private String rcvr;
    private String seqn;
    private String ddtm;
    private String type;          // 固定为 DFOE
    private String styp;          // 子类型：DFIE/DFDL/DFDE

    // ===== 航班标识 =====
    private String flid;
    private String ffid;
    private String fide;

    // ===== 整表同步专用（DFDL） =====
    private String dltp;          // 下载类型
    private String recd;          // 记录总数

    // ===== 整个 XML 存为 JSON =====
    private String rawData;

    // ===== 时间戳 =====
    private LocalDateTime createTime;
}