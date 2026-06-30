package com.binair.admin.context;

import com.binair.admin.entity.User;

/**
 * ThreadLocal 工具类 — 存储当前请求的用户信息
 */
public class UserContext {

    private static final ThreadLocal<User> USER_HOLDER = new ThreadLocal<>();

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
     * 获取当前用户职级
     */
    public static Integer getStatus() {
        User user = USER_HOLDER.get();
        return user != null ? user.getStatus() : null;
    }

    /**
     * 清理 ThreadLocal，防止内存泄漏
     */
    public static void clear() {
        USER_HOLDER.remove();
    }
}
