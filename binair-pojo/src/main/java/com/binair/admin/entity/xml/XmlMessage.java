package com.binair.admin.entity.xml;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 通用 XML 解析结果
 * <p>
 * META 解析为 Meta 对象，body（DFLT/APOT/CRFT）存为 JSON 字符串
 */
@Data
@AllArgsConstructor
public class XmlMessage {

    /** 消息头 */
    private Meta meta;

    /** body 内容的 JSON 字符串（DFLT / APOT / CRFT） */
    private String bodyJson;
}
