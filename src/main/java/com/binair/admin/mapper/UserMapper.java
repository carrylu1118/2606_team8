package com.binair.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.binair.admin.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    /**
     * 根据用户名查询员工
     */
    @Select("select * from sys_user where username = #{username}")
    User getByUsername(String username);

    /**
     * 查询用户的角色编码列表
     */
    @Select("SELECT r.role_code FROM sys_role r " +
            "JOIN sys_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId}")
    List<String> selectRoleCodesByUserId(Long userId);

    /**
     * 给用户分配角色
     */
    @Insert("INSERT INTO sys_user_role (user_id, role_id) VALUES (#{userId}, #{roleId})")
    int insertUserRole(Long userId, Long roleId);
}
