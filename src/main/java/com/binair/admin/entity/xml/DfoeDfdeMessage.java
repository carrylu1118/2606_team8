package com.binair.admin.entity.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "MSG")
public class DfoeDfdeMessage {

    @JacksonXmlProperty(localName = "META")
    private Meta meta;

    @JacksonXmlProperty(localName = "DFLT")
    private Dflt dflt;

    @Data
    public static class Dflt {
        @JacksonXmlProperty(localName = "FLID")
        private String flid;      // 航班唯一编号

        @JacksonXmlProperty(localName = "FFID")
        private String ffid;      // 航班标识（旧格式）

        @JacksonXmlProperty(localName = "FIDE")
        private String fide;      // 航班标识（新格式，推荐）
    }
}