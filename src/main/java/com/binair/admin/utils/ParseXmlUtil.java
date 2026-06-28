package com.binair.admin.utils;

import com.binair.admin.entity.xml.BaseApueMessage;
import com.binair.admin.entity.xml.DfmeAfidMessage;
import com.binair.admin.entity.xml.DfoeDfdeMessage;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

@Slf4j
public class ParseXmlUtil {

    private static final XmlMapper xmlMapper = new XmlMapper();

    static {
        // 忽略未知字段（防止XML里有我们没定义的标签）
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 生成XML声明头
        xmlMapper.configure(
                com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator.Feature.WRITE_XML_DECLARATION,
                true
        );
    }

    /**
     * 统一解析入口，根据文件自动判断类型
     */
    public static Object parseXml(File file) throws IOException {
        String fileName = file.getName();

        if (fileName.startsWith("BASE-")) {
            return parseBase(file);
        } else if (fileName.startsWith("DFME-")) {
            return parseDfme(file);
        } else if (fileName.startsWith("DFOE-") || fileName.startsWith("DF0E-")) {
            // 注意：你截图里是 DF0E（数字0），不是 DFOE（字母O）
            // 所以两个都要匹配
            return parseDfoe(file);
        } else {
            log.warn("未知类型的XML文件: {}", fileName);
            return null;
        }
    }

    /**
     * 解析 BASE 类型
     */
    public static BaseApueMessage parseBase(File file) throws IOException {
        return xmlMapper.readValue(file, BaseApueMessage.class);
    }

    /**
     * 解析 DFME 类型
     */
    public static DfmeAfidMessage parseDfme(File file) throws IOException {
        return xmlMapper.readValue(file, DfmeAfidMessage.class);
    }

    /**
     * 解析 DFOE 类型
     */
    public static DfoeDfdeMessage parseDfoe(File file) throws IOException {
        return xmlMapper.readValue(file, DfoeDfdeMessage.class);
    }
}