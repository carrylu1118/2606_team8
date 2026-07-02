package com.binair.admin.dto;

import lombok.Data;

@Data
public class FlightPageQueryDTO {

    /** 关键字（航班号/目的地模糊匹配） */
    private String keyword;

    /** 状态编码筛选 */
    private String status;

    /** 开始日期（yyyy-MM-dd） */
    private String startDate;

    /** 结束日期（yyyy-MM-dd） */
    private String endDate;

    /** 页码 */
    private int page = 1;

    /** 每页显示记录数 */
    private int pageSize = 10;
}
