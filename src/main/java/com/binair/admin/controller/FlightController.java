package com.binair.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.binair.admin.context.UserContext;
import com.binair.admin.dto.FlightPageQueryDTO;
import com.binair.admin.entity.FlightMaster;
import com.binair.admin.result.PageResult;
import com.binair.admin.result.Result;
import com.binair.admin.service.FlightMasterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 航班查询控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private final FlightMasterService flightMasterService;

    public FlightController(FlightMasterService flightMasterService) {
        this.flightMasterService = flightMasterService;
    }

    /**
     * 分页查询航班列表
     */
    @PostMapping("/page")
    public Result<PageResult> page(@RequestBody FlightPageQueryDTO dto) {
        log.info("航班分页查询：keyword={}, status={}, page={}/{}",
                dto.getKeyword(), dto.getStatus(), dto.getPage(), dto.getPageSize());

        Page<FlightMaster> page = new Page<>(dto.getPage(), dto.getPageSize());
        Page<FlightMaster> result = flightMasterService.pageQuery(
                page, dto.getKeyword(), dto.getStatus(),
                dto.getStartDate(), dto.getEndDate()
        );

        PageResult pageResult = new PageResult(result.getTotal(), result.getRecords());
        return Result.success(pageResult);
    }

    /**
     * 航班变更申请（需要航班管理员或以上权限）
     */
    @PostMapping("/change")
    public Result<String> change() {
        List<String> roles = UserContext.getRoles();
        if (roles == null || (!roles.contains("ROLE_MANAGER") && !roles.contains("ROLE_AUDITOR"))) {
            return Result.error("权限不足，需要航班管理员或以上权限");
        }
        // TODO 变更逻辑...
        return Result.success();
    }

    /**
     * 航班变更审核（需要审核人员权限）
     */
    @PostMapping("/audit")
    public Result<String> audit() {
        List<String> roles = UserContext.getRoles();
        if (roles == null || !roles.contains("ROLE_AUDITOR")) {
            return Result.error("权限不足，需要审核人员权限");
        }
        // TODO 审核逻辑...
        return Result.success();
    }
}
