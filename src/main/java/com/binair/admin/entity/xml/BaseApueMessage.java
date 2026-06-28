package com.binair.admin.entity.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "MSG")
public class BaseApueMessage {

    @JacksonXmlProperty(localName = "META")
    private Meta meta;

    @JacksonXmlProperty(localName = "APOT")
    private Apot apot;

    @Data
    public static class Apot {
        @JacksonXmlProperty(localName = "CODE")
        private String code;      // 机场三字码

        @JacksonXmlProperty(localName = "FRCD")
        private String frcd;      // 机场四字码

        @JacksonXmlProperty(localName = "APAT")
        private String apat;      // 机场属性：2403=国内

        @JacksonXmlProperty(localName = "CNNM")
        private String cnnm;      // 中文名称

        @JacksonXmlProperty(localName = "ENNM")
        private String ennm;      // 英文名称

        @JacksonXmlProperty(localName = "AISO")
        private String aiso;      // 是否开启：O=开启

        @JacksonXmlProperty(localName = "APSN")
        private String apsn;      // 航站简称
    }
}