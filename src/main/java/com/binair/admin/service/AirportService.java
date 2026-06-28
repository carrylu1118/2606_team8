package com.binair.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.binair.admin.entity.Airport;
import com.binair.admin.entity.xml.BaseApueMessage;
import com.binair.admin.mapper.AirportMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 机场数据服务 — 处理 BASE-APUE 消息 → airport_base 表
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AirportService {

    private final AirportMapper airportMapper;

    /**
     * 保存机场数据：以四字码为唯一键，存在则更新，不存在则新增
     */
    public void save(BaseApueMessage msg) {
        String frcd = msg.getApot().getFrcd();

        Airport existing = airportMapper.selectOne(
                new LambdaQueryWrapper<Airport>().eq(Airport::getFrcd, frcd)
        );

        if (existing != null) {
            existing.setCode(msg.getApot().getCode());
            existing.setApat(msg.getApot().getApat());
            existing.setCnnm(msg.getApot().getCnnm());
            existing.setEnnm(msg.getApot().getEnnm());
            existing.setAiso(msg.getApot().getAiso());
            existing.setApsn(msg.getApot().getApsn());
            existing.setMetaSndr(msg.getMeta().getSndr());
            existing.setMetaSeqn(msg.getMeta().getSeqn());
            existing.setMetaDdtm(msg.getMeta().getDdtm());
            airportMapper.updateById(existing);
            log.info("更新机场: {} ({})", msg.getApot().getCode(), msg.getApot().getCnnm());
        } else {
            Airport airport = new Airport();
            airport.setCode(msg.getApot().getCode());
            airport.setFrcd(frcd);
            airport.setApat(msg.getApot().getApat());
            airport.setCnnm(msg.getApot().getCnnm());
            airport.setEnnm(msg.getApot().getEnnm());
            airport.setAiso(msg.getApot().getAiso());
            airport.setApsn(msg.getApot().getApsn());
            airport.setMetaSndr(msg.getMeta().getSndr());
            airport.setMetaSeqn(msg.getMeta().getSeqn());
            airport.setMetaDdtm(msg.getMeta().getDdtm());
            airport.setCreateTime(LocalDateTime.now());
            airportMapper.insert(airport);
            log.info("新增机场: {} ({})", msg.getApot().getCode(), msg.getApot().getCnnm());
        }
    }
}
