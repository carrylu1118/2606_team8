package com.binair.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.binair.admin.entity.FlightAfid;
import com.binair.admin.entity.xml.DfmeAfidMessage;
import com.binair.admin.mapper.FlightAfidMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 航班衔接服务 — 处理 DFME-AFID 消息 → flight_afid 表
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AfidService {

    private final FlightAfidMapper flightAfidMapper;

    /**
     * 保存航班衔接信息：以 flid 为唯一键，存在则更新，不存在则新增
     */
    public void save(DfmeAfidMessage msg) {
        String flid = msg.getDflt().getFlid();

        FlightAfid existing = flightAfidMapper.selectOne(
                new LambdaQueryWrapper<FlightAfid>().eq(FlightAfid::getFlid, flid)
        );

        if (existing != null) {
            existing.setFfid(msg.getDflt().getFfid());
            existing.setFide(msg.getDflt().getFide());
            existing.setAfid(msg.getDflt().getAfid());
            existing.setMetaSndr(msg.getMeta().getSndr());
            existing.setMetaSeqn(msg.getMeta().getSeqn());
            existing.setMetaDdtm(msg.getMeta().getDdtm());
            flightAfidMapper.updateById(existing);
            log.info("更新航班衔接: flid={}, afid={}", flid, msg.getDflt().getAfid());
        } else {
            FlightAfid entity = new FlightAfid();
            entity.setFlid(flid);
            entity.setFfid(msg.getDflt().getFfid());
            entity.setFide(msg.getDflt().getFide());
            entity.setAfid(msg.getDflt().getAfid());
            entity.setMetaSndr(msg.getMeta().getSndr());
            entity.setMetaSeqn(msg.getMeta().getSeqn());
            entity.setMetaDdtm(msg.getMeta().getDdtm());
            entity.setCreateTime(LocalDateTime.now());
            flightAfidMapper.insert(entity);
            log.info("新增航班衔接: flid={}, afid={}", flid, msg.getDflt().getAfid());
        }
    }
}
