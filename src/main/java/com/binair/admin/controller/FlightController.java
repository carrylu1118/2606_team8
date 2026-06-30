package com.binair.admin.controller;

import com.binair.admin.context.UserContext;
import com.binair.admin.dto.FlightPageQueryDTO;
import com.binair.admin.entity.User;
import com.binair.admin.result.PageResult;
import com.binair.admin.result.Result;
import com.binair.admin.service.FlightService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.jdbc.Null;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping("/page")
    public Result<PageResult> page(@RequestBody FlightPageQueryDTO flightPageQueryDTO) {
        // 从拦截器获取当前用户
        User currentUser = UserContext.getUser();
        Integer status = currentUser.getStatus();

        // 判断权限：只有 status >= 1 才能查（所有登录用户都能查）
        if (status < 1) {
            return Result.error("权限不足");
        }
        //TODO 查询逻辑...
        log.info("员工分页查询，参数为:{}",flightPageQueryDTO);
        PageResult pageResult = flightService.page(flightPageQueryDTO);
        return Result.success(pageResult);
    }

    @PostMapping("/change")
    public Result<Null> change() {
        User currentUser = UserContext.getUser();
        Integer status = currentUser.getStatus();

        // 只有管理员（status>=2）才能申请变更
        if (status < 2) {
            return Result.error("权限不足，需要航班管理员或以上权限");
        }
        // TODO 变更逻辑...
        return Result.success();
    }

    @PostMapping("/audit")
    public Result<Null> audit() {
        User currentUser = UserContext.getUser();
        Integer status = currentUser.getStatus();

        // 只有审核人员（status>=3）才能审核
        if (status < 3) {
            return Result.error("权限不足，需要审核人员权限");
        }
        // TODO 审核逻辑...
        return Result.success();
    }
}
