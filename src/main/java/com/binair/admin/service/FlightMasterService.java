package com.binair.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.binair.admin.entity.FlightMaster;
import com.binair.admin.vo.FlightDynamicVO;

import java.time.LocalDateTime;

/**
 * 航班主表 Service 接口
 */
public interface FlightMasterService extends IService<FlightMaster> {

    /**
     * 存在则更新，不存在则插入
     */
    void updateOrInsert(FlightMaster master);

    /**
     * 根据 fide 更新航班状态（按优先级防降级）
     */
    void updateStatusByFide(String fide, String statusCode, String statusName, LocalDateTime actualTime);

    /**
     * 根据 fide 更新计划时间
     */
    void updatePlanTimeByFide(String fide, LocalDateTime planDepart, LocalDateTime planArrive);

    /**
     * 根据 fide 更新目的地
     */
    void updateDestinationByFide(String fide, String destCode, String destName);

    /**
     * 分页查询航班列表（管理员）
     */
    Page<FlightMaster> pageQuery(Page<FlightMaster> page, String keyword, String status, String startDate, String endDate);

    /**
     * 分页查询航班动态（面向旅客，只显示有登机口和值机柜台的）
     */
    Page<FlightDynamicVO> pageDynamicQuery(int pageNum, int pageSize, String keyword, String status, String startDate, String endDate);

    /**
     * 分页查询全部航班（面向旅客，无资源过滤）
     */
    Page<FlightDynamicVO> pageAllQuery(int pageNum, int pageSize, String keyword, String status, String startDate, String endDate);
}
