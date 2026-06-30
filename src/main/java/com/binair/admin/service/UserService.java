package com.binair.admin.service;

import com.binair.admin.dto.UserLoginDTO;
import com.binair.admin.dto.UserRegisterDTO;
import com.binair.admin.entity.User;

public interface UserService {
    /**
     * 员工登录
     * @param userLoginDto 用户登录数据
     * @return 实体对象
     */
    User login(UserLoginDTO userLoginDto);

    /**
     * 用户注册
     * @param userRegisterDTO 用户注册数据
     * @return 注册成功的用户实体
     */
    User register(UserRegisterDTO userRegisterDTO);
}
