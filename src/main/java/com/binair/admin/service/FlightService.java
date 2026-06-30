package com.binair.admin.service;

import com.binair.admin.dto.FlightPageQueryDTO;
import com.binair.admin.result.PageResult;

public interface FlightService {
    public PageResult page(FlightPageQueryDTO flightPageQueryDTO);
}
