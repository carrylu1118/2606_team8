package com.binair.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * XML 文件处理记录 — 映射到 xml_process_record 表
 */
@Data
@Builder
@TableName("xml_process_record")
public class XmlProcessRecord {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /** 文件名 */
    private String fileName;

    /** 文件完整路径 */
    private String filePath;

    /** 文件大小 */
    private Long fileSize;

    /** 文件最后修改时间 */
    private Long fileLastModified;

    /** 1=已处理 2=处理失败 */
    private Integer processStatus;

    /** 处理时间 */
    private LocalDateTime processTime;
}
