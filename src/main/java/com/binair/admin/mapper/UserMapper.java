package com.binair.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.binair.admin.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    /**
     * 根据用户名查询员工
     */
    @Select("select * from sys_user where username = #{username}")
    User getByUsername(String username);
}
