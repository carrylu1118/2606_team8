package com.binair.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * TB_FATT — 航班属性字典
 */
@Data
@TableName("TB_FATT")
public class FattEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String code;
    private String name;
    private LocalDateTime createTime;
}
