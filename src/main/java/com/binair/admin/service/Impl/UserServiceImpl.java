package com.binair.admin.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.binair.admin.constant.MessageConstant;
import com.binair.admin.dto.UserLoginDTO;
import com.binair.admin.dto.UserRegisterDTO;
import com.binair.admin.entity.SysRole;
import com.binair.admin.entity.User;
import com.binair.admin.exception.AccountLockedException;
import com.binair.admin.exception.AccountNotFoundException;
import com.binair.admin.exception.PasswordErrorException;
import com.binair.admin.mapper.SysRoleMapper;
import com.binair.admin.mapper.UserMapper;
import com.binair.admin.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final SysRoleMapper sysRoleMapper;

    public UserServiceImpl(UserMapper userMapper, SysRoleMapper sysRoleMapper) {
        this.userMapper = userMapper;
        this.sysRoleMapper = sysRoleMapper;
    }

    /**
     * 员工登录
     *
     * @param userLoginDto 用户登录数据
     * @return 实体对象
     */
    public User login(UserLoginDTO userLoginDto) {
        String username = userLoginDto.getUsername();
        String password = userLoginDto.getPassword();

        // 1、根据用户名查询数据库中的数据
        User user = userMapper.getByUsername(username);

        // 2、处理各种异常情况（用户名不存在、密码不对、账号被禁用）
        if (user == null) {
            log.warn("用户名不存在：username={}", username);
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        // 检查账号是否被禁用
        if (user.getStatus() != null && user.getStatus() == 0) {
            log.warn("账号已被禁用：username={}", username);
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        // 密码比对：对前端传入的密码进行 MD5 加密后与数据库中密码比较
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!md5Password.equals(user.getPassword())) {
            log.warn("密码错误：username={}", username);
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        // 3、返回实体对象
        return user;
    }

    /**
     * 用户注册
     *
     * @param userRegisterDTO 用户注册数据
     * @return 注册成功的用户实体
     */
    public User register(UserRegisterDTO userRegisterDTO) {
        // 1、检查用户名是否已存在
        User existing = userMapper.getByUsername(userRegisterDTO.getUsername());
        if (existing != null) {
            throw new RuntimeException(MessageConstant.ALREADY_EXISTS);
        }

        // 2、密码 MD5 加密后保存
        User user = User.builder()
                .username(userRegisterDTO.getUsername())
                .password(DigestUtils.md5DigestAsHex(userRegisterDTO.getPassword().getBytes()))
                .email(userRegisterDTO.getEmail())
                .phone(userRegisterDTO.getPhone())
                .realName(userRegisterDTO.getRealName())
                .status(1)
                .createTime(LocalDateTime.now())
                .build();

        // 3、插入用户表
        userMapper.insert(user);

        // 4、分配默认角色 ROLE_USER
        LambdaQueryWrapper<SysRole> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.eq(SysRole::getRoleCode, "ROLE_USER");
        SysRole defaultRole = sysRoleMapper.selectOne(roleWrapper);
        if (defaultRole != null) {
            userMapper.insertUserRole(user.getId(), defaultRole.getId());
            log.info("用户 {} 已分配默认角色 ROLE_USER", user.getUsername());
        } else {
            log.warn("默认角色 ROLE_USER 不存在，用户 {} 未分配角色", user.getUsername());
        }

        return user;
    }

}
