package com.binair.admin;

import com.binair.admin.entity.xml.BaseApueMessage;
import com.binair.admin.entity.xml.DfmeAfidMessage;
import com.binair.admin.entity.xml.DfoeDfdeMessage;
import com.binair.admin.utils.ParseXmlUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.File;

@Slf4j
public class XmlParseTest {

    private final String BASE_PATH = "E:/develop/flight_db/";

    @Test
    public void testParseBaseApue() throws Exception {
        File file = new File(BASE_PATH + "20170604/BASE-APUE-20170604005428.xml");
        BaseApueMessage msg = ParseXmlUtil.parseBase(file);

        log.info("=== BASE-APUE 解析结果 ===");
        log.info("消息类型: {} - {}", msg.getMeta().getType(), msg.getMeta().getStyp());
        log.info("机场三字码: {}", msg.getApot().getCode());
        log.info("机场四字码: {}", msg.getApot().getFrcd());
        log.info("机场名称: {}", msg.getApot().getCnnm());
        log.info("机场简称: {}", msg.getApot().getApsn());
    }

    @Test
    public void testParseDfmeAfid() throws Exception {
        File file = new File(BASE_PATH + "20170601/DFME-AFID-20170601105143.xml");
        DfmeAfidMessage msg = ParseXmlUtil.parseDfme(file);

        log.info("=== DFME-AFID 解析结果 ===");
        log.info("消息类型: {} - {}", msg.getMeta().getType(), msg.getMeta().getStyp());
        log.info("航班ID: {}", msg.getDflt().getFlid());
        log.info("航班标识: {}", msg.getDflt().getFide());
        log.info("衔接航班ID: {}", msg.getDflt().getAfid());
    }

    @Test
    public void testParseDfoeDfde() throws Exception {
        File file = new File(BASE_PATH + "20170602/DFOE-DFDE-20170602215733.xml");
        DfoeDfdeMessage msg = ParseXmlUtil.parseDfoe(file);

        log.info("=== DFOE-DFDE 解析结果 ===");
        log.info("消息类型: {} - {}", msg.getMeta().getType(), msg.getMeta().getStyp());
        log.info("航班ID: {}", msg.getDflt().getFlid());
        log.info("航班标识: {}", msg.getDflt().getFide());
    }

    @Test
    public void testParseByAutoDetect() throws Exception {
        // 自动识别类型
        File file = new File(BASE_PATH + "20170604/BASE-APUE-20170604005428.xml");
        Object result = ParseXmlUtil.parseXml(file);
        log.info("自动解析结果: {}", result.getClass().getSimpleName());
    }
}