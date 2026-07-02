package com.binair.admin;

import com.binair.admin.utils.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RedisTest {

    @Autowired
    private RedisUtil redisUtil;

    @Test
    public void testSet() {
        redisUtil.set("test:name", "2606_team8");
        System.out.println("✅ Redis 写入成功！");
    }

    @Test
    public void testGet() {
        Object value = redisUtil.get("test:name");
        System.out.println("📖 读取结果: " + value);
    }

    @Test
    public void testSetWithExpire() throws InterruptedException {
        redisUtil.set("test:expire", "我5秒后消失", 5, java.util.concurrent.TimeUnit.SECONDS);
        System.out.println("✅ 已写入（5秒过期）");

        // 立即读一次
        System.out.println("第一次读取: " + redisUtil.get("test:expire"));

        // 等6秒后再读一次
        Thread.sleep(6000);
        System.out.println("第二次读取(6秒后): " + redisUtil.get("test:expire"));
    }

    @Test
    public void testSetOps() {
        // Set 操作测试
        redisUtil.sAdd("test:set", "A", "B", "C");
        System.out.println("✅ Set 写入成功");
    }

    @Test
    public void testHashOps() {
        // Hash 操作测试
        redisUtil.hPut("test:hash", "name", "张三");
        redisUtil.hPut("test:hash", "age", "25");
        System.out.println("✅ Hash 写入成功");
        System.out.println("读取 name: " + redisUtil.hGet("test:hash", "name"));
    }

    @Test
    public void testIncrement() {
        Long count = redisUtil.increment("test:count");
        System.out.println("✅ 当前计数: " + count);
    }
}