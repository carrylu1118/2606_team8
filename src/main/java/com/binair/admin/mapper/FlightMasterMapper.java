package com.binair.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.binair.admin.entity.FlightMaster;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * 航班主表 Mapper
 */
@Mapper
public interface FlightMasterMapper extends BaseMapper<FlightMaster> {

    /** 根据 fide 更新航班主表 */
    @Update("UPDATE flight_master SET " +
            "flid = #{flid}, flight_no = #{flightNo}, airline = #{airline}, " +
            "departure = #{departure}, destination = #{destination}, destination_code = #{destinationCode}, " +
            "plan_depart_time = #{planDepartTime}, plan_arrive_time = #{planArriveTime}, " +
            "actual_depart_time = #{actualDepartTime}, actual_arrive_time = #{actualArriveTime}, " +
            "status_code = #{statusCode}, status_name = #{statusName}, " +
            "update_time = NOW() " +
            "WHERE fide = #{fide}")
    int updateByFide(FlightMaster master);
}
