package com.binair.admin.interceptor;

import com.binair.admin.constant.MessageConstant;
import com.binair.admin.context.UserContext;
import com.binair.admin.entity.User;
import com.binair.admin.mapper.UserMapper;
import com.binair.admin.result.Result;
import com.binair.admin.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT Token 校验拦截器
 * <p>
 * 校验请求头中的 Bearer Token，解析用户名后查询数据库获取用户信息并存入 ThreadLocal
 */
@Slf4j
@Component
public class JwtTokenInterceptor implements HandlerInterceptor {

    @Autowired
    private UserMapper userMapper;

    @Value("${jwt.secret-key}")
    private String jwtSecretKey;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1、从请求头获取 Token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("请求头缺少 Authorization 或格式不正确：{}", request.getRequestURI());
            writeUnauthorized(response, "用户未登录");
            return false;
        }

        String token = authHeader.substring(7);

        // 2、解析 JWT 获取用户名
        Claims claims;
        try {
            claims = JwtUtil.parseJWT(jwtSecretKey, token);
        } catch (Exception e) {
            log.warn("JWT 解析失败：{}", e.getMessage());
            writeUnauthorized(response, "Token 无效或已过期");
            return false;
        }

        String username = (String) claims.get("username");
        if (username == null) {
            log.warn("JWT 中缺少 username 字段");
            writeUnauthorized(response, "Token 无效");
            return false;
        }

        // 3、查询用户信息
        User user = userMapper.getByUsername(username);
        if (user == null) {
            log.warn("用户不存在：username={}", username);
            writeUnauthorized(response, MessageConstant.ACCOUNT_NOT_FOUND);
            return false;
        }

        // 检查账号是否被禁用
        if (user.getStatus() != null && user.getStatus() == 0) {
            log.warn("账号已被禁用：username={}", username);
            writeUnauthorized(response, MessageConstant.ACCOUNT_LOCKED);
            return false;
        }

        // 4、查询用户角色列表
        java.util.List<String> roleCodes = userMapper.selectRoleCodesByUserId(user.getId());

        // 5、存入 ThreadLocal
        UserContext.setUser(user);
        UserContext.setRoles(roleCodes);
        log.debug("用户认证成功：username={}, userId={}, roles={}",
                user.getUsername(), user.getId(), roleCodes);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清理 ThreadLocal，防止内存泄漏
        UserContext.clear();
    }

    /**
     * 写 401 未授权响应（JSON 格式）
     */
    private void writeUnauthorized(HttpServletResponse response, String message) throws Exception {
        response.setStatus(401);
        response.setContentType("application/json;charset=UTF-8");
        Result<Object> result = Result.error(message);
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(result));
    }
}
