package com.binair.admin.dto;

import lombok.Data;

@Data
public class FlightPageQueryDTO {

    private String name;

    //页码
    private int page;

    //每页显示记录数
    private int pageSize;
}
