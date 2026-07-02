package com.binair.admin.utils;

import com.binair.admin.entity.xml.Meta;
import com.binair.admin.entity.xml.XmlMessage;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * 通用 XML 解析器
 * <p>
 * 不依赖固定 DTO，所有 MSG 类型的 XML 统一解析为 XmlMessage（Meta + body JSON）。
 * 支持 BASE/DFME/DFOE 全部 30 种子类型。
 */
@Slf4j
public class XmlParser {

    private static final XmlMapper xmlMapper = new XmlMapper();
    private static final ObjectMapper jsonMapper = new ObjectMapper();

    static {
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 解析 XML 文件为 XmlMessage
     *
     * @param file XML 文件
     * @return XmlMessage（meta + bodyJson），解析失败返回 null
     */
    public static XmlMessage parse(File file) {
        try {
            JsonNode root = xmlMapper.readTree(file);

            // 解析 META
            JsonNode metaNode = root.get("META");
            if (metaNode == null) {
                log.warn("XML 缺少 META 节点: {}", file.getName());
                return null;
            }
            Meta meta = xmlMapper.treeToValue(metaNode, Meta.class);

            // 查找 body 节点（不是 META 的那个，可能是 DFLT/APOT/CRFT）
            String bodyJson = null;
            Iterator<String> fieldNames = root.fieldNames();
            while (fieldNames.hasNext()) {
                String name = fieldNames.next();
                if (!"META".equals(name)) {
                    bodyJson = jsonMapper.writeValueAsString(root.get(name));
                    break;
                }
            }

            return new XmlMessage(meta, bodyJson);
        } catch (IOException e) {
            log.error("XML 解析失败: {}", file.getName(), e);
            return null;
        }
    }
}
