package com.binair.admin.entity.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "META")
public class Meta {

    @JacksonXmlProperty(localName = "SNDR")
    private String sndr;          // 消息发送者

    @JacksonXmlProperty(localName = "RCVR")
    private String rcvr;          // 消息接收者

    @JacksonXmlProperty(localName = "SEQN")
    private String seqn;          // 消息序号

    @JacksonXmlProperty(localName = "DDTM")
    private String ddtm;          // 发送时间

    @JacksonXmlProperty(localName = "TYPE")
    private String type;          // 消息大类型：BASE/DFME/DFOE

    @JacksonXmlProperty(localName = "STYP")
    private String styp;          // 消息子类型：APUE/AFID/DFDE
}