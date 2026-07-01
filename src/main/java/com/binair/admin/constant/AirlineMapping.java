package com.binair.admin.constant;

import java.util.Map;

/**
 * 航空公司二字码 → 中文全称映射（共 40 个）
 */
public class AirlineMapping {

    private static final Map<String, String> MAP = Map.ofEntries(
            Map.entry("3U", "四川航空"),
            Map.entry("8C", "西部航空"),
            Map.entry("8L", "红土航空"),
            Map.entry("9C", "春秋航空"),
            Map.entry("A6", "湖南航空"),
            Map.entry("AQ", "九元航空"),
            Map.entry("BK", "奥凯航空"),
            Map.entry("CA", "中国国际航空"),
            Map.entry("CZ", "中国南方航空"),
            Map.entry("DR", "瑞丽航空"),
            Map.entry("DU", "海南航空"),
            Map.entry("DZ", "东海航空"),
            Map.entry("EU", "成都航空"),
            Map.entry("FM", "上海航空"),
            Map.entry("FU", "福州航空"),
            Map.entry("G5", "华夏航空"),
            Map.entry("GS", "天津航空"),
            Map.entry("GT", "桂林航空"),
            Map.entry("HU", "海南航空"),
            Map.entry("HX", "香港航空"),
            Map.entry("JD", "首都航空"),
            Map.entry("JL", "日本航空"),
            Map.entry("JR", "幸福航空"),
            Map.entry("KY", "昆明航空"),
            Map.entry("LS", "丽江航空"),
            Map.entry("MF", "厦门航空"),
            Map.entry("MU", "中国东方航空"),
            Map.entry("NS", "河北航空"),
            Map.entry("NX", "澳门航空"),
            Map.entry("OQ", "重庆航空"),
            Map.entry("PN", "西部航空"),
            Map.entry("RY", "江西航空"),
            Map.entry("SC", "山东航空"),
            Map.entry("TV", "西藏航空"),
            Map.entry("UQ", "乌鲁木齐航空"),
            Map.entry("UR", "乌鲁木齐航空"),
            Map.entry("Y8", "祥鹏航空"),
            Map.entry("YS", "扬子江航空"),
            Map.entry("ZH", "深圳航空")
    );

    /** 根据二字码获取中文全称，未知则返回原码 */
    public static String getName(String code) {
        if (code == null) return null;
        return MAP.getOrDefault(code.toUpperCase(), code);
    }
}
