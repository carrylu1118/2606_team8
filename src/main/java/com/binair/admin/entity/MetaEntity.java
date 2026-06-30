package com.binair.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * TB_META — 消息头表
 */
@Data
@TableName("TB_META")
public class MetaEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String sndr;
    private String rcvr;
    private String seqn;
    private String ddtm;
    private String type;
    private String styp;
    private LocalDateTime createTime;
}
