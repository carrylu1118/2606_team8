package com.binair.admin.context;

import com.binair.admin.entity.User;

import java.util.List;

/**
 * ThreadLocal 工具类 — 存储当前请求的用户信息及角色列表
 */
public class UserContext {

    private static final ThreadLocal<User> USER_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<List<String>> ROLES_HOLDER = new ThreadLocal<>();

    // ==================== 用户信息 ====================

    /**
     * 存入当前用户
     */
    public static void setUser(User user) {
        USER_HOLDER.set(user);
    }

    /**
     * 获取当前用户
     */
    public static User getUser() {
        return USER_HOLDER.get();
    }

    /**
     * 获取当前用户 ID
     */
    public static Long getUserId() {
        User user = USER_HOLDER.get();
        return user != null ? user.getId() : null;
    }

    /**
     * 获取当前用户启用状态（1=启用，0=禁用）
     */
    public static Integer getStatus() {
        User user = USER_HOLDER.get();
        return user != null ? user.getStatus() : null;
    }

    // ==================== 角色信息 ====================

    /**
     * 存入当前用户的角色编码列表
     */
    public static void setRoles(List<String> roles) {
        ROLES_HOLDER.set(roles);
    }

    /**
     * 获取当前用户的角色编码列表，如 ["ROLE_USER", "ROLE_MANAGER"]
     */
    public static List<String> getRoles() {
        return ROLES_HOLDER.get();
    }

    // ==================== 清理 ====================

    /**
     * 清理 ThreadLocal，防止内存泄漏
     */
    public static void clear() {
        USER_HOLDER.remove();
        ROLES_HOLDER.remove();
    }
}
