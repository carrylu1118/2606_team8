package com.binair.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("dfme_message")
public class DfmeMessage {

    @TableId(type = IdType.AUTO)
    private Long id;

    // ===== 消息头（所有 DFME 共有） =====
    private String sndr;          // 发送者
    private String rcvr;          // 接收者
    private String seqn;          // 序号
    private String ddtm;          // 消息时间
    private String type;          // 固定为 DFME
    private String styp;          // 子类型：STLS/GTLS/BLLS/CKLS/FRTT...

    // ===== 航班标识（所有 DFME 共有） =====
    private String flid;          // 航班唯一编号
    private String ffid;          // 航班标识（旧格式）
    private String fide;          // 航班标识（新格式）

    // ===== 其他通用字段 =====
    private String awcd;          // 航空公司三字码
    private String flno;          // 航班号
    private String mfid;          // 主航班ID
    private String mffi;          // 主航班标识

    // ===== 整个 XML 存为 JSON（备用，存所有字段） =====
    private String rawData;       // 完整 XML 转 JSON

    // ===== 时间戳 =====
    private LocalDateTime createTime;
}