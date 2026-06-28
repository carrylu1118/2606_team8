package com.binair.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.binair.admin.entity.FlightDelete;
import com.binair.admin.entity.xml.DfoeDfdeMessage;
import com.binair.admin.mapper.FlightDeleteMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 航班终止服务 — 处理 DFOE-DFDE 消息 → flight_delete 表
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteService {

    private final FlightDeleteMapper flightDeleteMapper;

    /**
     * 处理航班终止消息：以 flid 为唯一键，存在则更新，不存在则新增
     */
    public void save(DfoeDfdeMessage msg) {
        String flid = msg.getDflt().getFlid();

        FlightDelete existing = flightDeleteMapper.selectOne(
                new LambdaQueryWrapper<FlightDelete>().eq(FlightDelete::getFlid, flid)
        );

        if (existing != null) {
            existing.setFfid(msg.getDflt().getFfid());
            existing.setFide(msg.getDflt().getFide());
            existing.setMetaSndr(msg.getMeta().getSndr());
            existing.setMetaSeqn(msg.getMeta().getSeqn());
            existing.setMetaDdtm(msg.getMeta().getDdtm());
            flightDeleteMapper.updateById(existing);
            log.info("更新航班终止状态: flid={}", flid);
        } else {
            FlightDelete entity = new FlightDelete();
            entity.setFlid(flid);
            entity.setFfid(msg.getDflt().getFfid());
            entity.setFide(msg.getDflt().getFide());
            entity.setMetaSndr(msg.getMeta().getSndr());
            entity.setMetaSeqn(msg.getMeta().getSeqn());
            entity.setMetaDdtm(msg.getMeta().getDdtm());
            entity.setCreateTime(LocalDateTime.now());
            flightDeleteMapper.insert(entity);
            log.info("新增航班终止记录: flid={}", flid);
        }
    }
}
