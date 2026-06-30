package com.binair.admin.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.binair.admin.entity.FlightMaster;
import com.binair.admin.mapper.FlightMasterMapper;
import com.binair.admin.service.FlightMasterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 航班主表 Service 实现
 * <p>
 * 状态防降级优先级：CAN(取消) > RTN(返航) > DLY(延误) > DEP(起飞) > ARR(到达) > PLAN(计划)
 */
@Slf4j
@Service
public class FlightMasterServiceImpl extends ServiceImpl<FlightMasterMapper, FlightMaster>
        implements FlightMasterService {

    /** 状态优先级映射（数值越大优先级越高） */
    private static final List<String> STATUS_PRIORITY = List.of("PLAN", "ARR", "DEP", "DLY", "RTN", "CAN");

    @Override
    public void updateOrInsert(FlightMaster master) {
        FlightMaster existing = getByFide(master.getFide());
        if (existing != null) {
            // 存在则合并更新：只更新非 null 字段
            if (master.getFlid() != null) existing.setFlid(master.getFlid());
            if (master.getFlightNo() != null) existing.setFlightNo(master.getFlightNo());
            if (master.getAirline() != null) existing.setAirline(master.getAirline());
            if (master.getDeparture() != null) existing.setDeparture(master.getDeparture());
            if (master.getDestination() != null) existing.setDestination(master.getDestination());
            if (master.getDestinationCode() != null) existing.setDestinationCode(master.getDestinationCode());
            if (master.getPlanDepartTime() != null) existing.setPlanDepartTime(master.getPlanDepartTime());
            if (master.getPlanArriveTime() != null) existing.setPlanArriveTime(master.getPlanArriveTime());
            if (master.getActualDepartTime() != null) existing.setActualDepartTime(master.getActualDepartTime());
            if (master.getActualArriveTime() != null) existing.setActualArriveTime(master.getActualArriveTime());
            if (master.getStatusCode() != null) existing.setStatusCode(master.getStatusCode());
            if (master.getStatusName() != null) existing.setStatusName(master.getStatusName());
            baseMapper.updateByFide(existing);
        } else {
            baseMapper.insert(master);
        }
    }

    @Override
    public void updateStatusByFide(String fide, String statusCode, String statusName, LocalDateTime actualTime) {
        FlightMaster existing = getByFide(fide);
        if (existing == null) {
            log.warn("航班不存在，跳过状态更新: fide={}", fide);
            return;
        }

        // 防降级：新状态优先级低于当前状态时跳过
        if (existing.getStatusCode() != null && !canUpgrade(existing.getStatusCode(), statusCode)) {
            log.debug("状态防降级：跳过 {} → {} (fide={})", existing.getStatusCode(), statusCode, fide);
            return;
        }

        existing.setStatusCode(statusCode);
        existing.setStatusName(statusName);
        if (actualTime != null) {
            if ("DEP".equals(statusCode)) {
                existing.setActualDepartTime(actualTime);
            } else if ("ARR".equals(statusCode)) {
                existing.setActualArriveTime(actualTime);
            }
        }
        baseMapper.updateByFide(existing);
        log.info("航班状态更新: fide={}, {}→{}", fide, existing.getStatusCode(), statusCode);
    }

    @Override
    public void updatePlanTimeByFide(String fide, LocalDateTime planDepart, LocalDateTime planArrive) {
        FlightMaster existing = getByFide(fide);
        if (existing == null) {
            log.warn("航班不存在，跳过计划时间更新: fide={}", fide);
            return;
        }
        existing.setPlanDepartTime(planDepart);
        existing.setPlanArriveTime(planArrive);
        baseMapper.updateByFide(existing);
    }

    @Override
    public void updateDestinationByFide(String fide, String destCode, String destName) {
        FlightMaster existing = getByFide(fide);
        if (existing == null) {
            log.warn("航班不存在，跳过目的地更新: fide={}", fide);
            return;
        }
        existing.setDestinationCode(destCode);
        existing.setDestination(destName);
        baseMapper.updateByFide(existing);
    }

    @Override
    public Page<FlightMaster> pageQuery(Page<FlightMaster> page, String keyword, String status,
                                         String startDate, String endDate) {
        LambdaQueryWrapper<FlightMaster> wrapper = new LambdaQueryWrapper<>();

        // 关键字模糊匹配：航班号 + 目的地
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w
                    .like(FlightMaster::getFlightNo, keyword)
                    .or()
                    .like(FlightMaster::getDestination, keyword)
            );
        }
        // 状态精确筛选
        if (status != null && !status.isBlank()) {
            wrapper.eq(FlightMaster::getStatusCode, status);
        }
        // 计划离港时间范围筛选
        if (startDate != null && !startDate.isBlank()) {
            wrapper.ge(FlightMaster::getPlanDepartTime, LocalDateTime.parse(startDate + "T00:00:00"));
        }
        if (endDate != null && !endDate.isBlank()) {
            wrapper.le(FlightMaster::getPlanDepartTime, LocalDateTime.parse(endDate + "T23:59:59"));
        }
        wrapper.orderByAsc(FlightMaster::getPlanDepartTime);

        return baseMapper.selectPage(page, wrapper);
    }

    // ==================== 内部辅助 ====================

    private FlightMaster getByFide(String fide) {
        return baseMapper.selectOne(
                new LambdaQueryWrapper<FlightMaster>().eq(FlightMaster::getFide, fide)
        );
    }

    /** 判断新状态优先级是否不低于当前状态 */
    private boolean canUpgrade(String currentStatus, String newStatus) {
        int currentIdx = STATUS_PRIORITY.indexOf(currentStatus);
        int newIdx = STATUS_PRIORITY.indexOf(newStatus);
        if (currentIdx < 0 || newIdx < 0) return true; // 未知状态允许
        return newIdx >= currentIdx;
    }
}
