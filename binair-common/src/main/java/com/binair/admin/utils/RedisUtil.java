package com.binair.admin.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    //存值
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    // 存值 + 过期时间
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    // 取值
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // 删除
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    // 是否存在
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    // 设置过期时间
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    // Set 操作（存集合）
    public Long sAdd(String key, Object... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    public Boolean sIsMember(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    // Hash 操作
    public void hPut(String key, String field, Object value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    public Object hGet(String key, String field) {
        return redisTemplate.opsForHash().get(key, field);
    }

    // 自增（用于计数）
    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(key);
    }
}