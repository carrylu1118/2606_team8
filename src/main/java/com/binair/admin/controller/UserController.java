package com.binair.admin.controller;

import com.binair.admin.dto.UserLoginDTO;
import com.binair.admin.dto.UserRegisterDTO;
import com.binair.admin.entity.User;
import com.binair.admin.result.Result;
import com.binair.admin.service.UserService;
import com.binair.admin.utils.JwtUtil;
import com.binair.admin.vo.UserLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @Value("${jwt.secret-key}")
    private String jwtSecretKey;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDto) {
        log.info("登录请求：username={}", userLoginDto.getUsername());
        // 1、调用 service 验证用户名密码
        User user = userService.login(userLoginDto);

        // 2、登录成功，生成 JWT
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("username", user.getUsername());
        String token = JwtUtil.createJWT(jwtSecretKey, jwtExpiration, claims);

        // 3、构建返回 VO
        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .userName(user.getUsername())
                .name(user.getRealName())
                .token(token)
                .build();

        log.info("登录成功：username={}", user.getUsername());
        return Result.success(userLoginVO);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<String> register(@RequestBody UserRegisterDTO userRegisterDTO) {
        log.info("注册请求：username={}", userRegisterDTO.getUsername());
        try {
            userService.register(userRegisterDTO);
            log.info("注册成功：username={}", userRegisterDTO.getUsername());
            return Result.success("注册成功");
        } catch (Exception e) {
            log.warn("注册失败：{}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }
}
