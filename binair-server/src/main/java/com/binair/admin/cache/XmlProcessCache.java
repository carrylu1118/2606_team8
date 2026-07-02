package com.binair.admin.cache;

import com.binair.admin.entity.XmlProcessRecord;
import com.binair.admin.mapper.XmlProcessRecordMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * XML 文件处理缓存
 * <p>
 * 启动时从 Redis SET 一次性加载所有已处理路径到本地 HashSet，
 * 后续 isProcessed() 纯内存判断 O(1)，不走网络。
 * Redis 为空时自动从 MySQL 回灌。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class XmlProcessCache {

    private static final String PROCESSED_KEY = "xml:processed:paths";
    private static final String ALL_PATHS_KEY = "xml:all:paths";
    private static final int BATCH_SIZE = 1000;

    private final StringRedisTemplate redisTemplate;
    private final XmlProcessRecordMapper processRecordMapper;

    /** 本地缓存，启动时一次性加载 */
    private final Set<String> processedPaths = new HashSet<>();

    @PostConstruct
    public void init() {
        // 1. 尝试从 Redis 加载
        try {
            Set<String> redisPaths = redisTemplate.opsForSet().members(PROCESSED_KEY);
            if (redisPaths != null && !redisPaths.isEmpty()) {
                processedPaths.addAll(redisPaths);
                log.info("从 Redis 加载已处理文件缓存: {} 条", processedPaths.size());
                return;
            }
            log.info("Redis 中无缓存数据，尝试从 MySQL 加载");
        } catch (Exception e) {
            log.warn("Redis 读取失败，降级从 MySQL 加载: {}", e.getMessage());
        }

        // 2. Redis 为空或不可用，从 MySQL 加载并回灌 Redis
        try {
            List<XmlProcessRecord> records = processRecordMapper.selectList(
                    new LambdaQueryWrapper<XmlProcessRecord>()
                            .eq(XmlProcessRecord::getProcessStatus, 1)
            );
            for (XmlProcessRecord r : records) {
                processedPaths.add(r.getFilePath());
            }
            log.info("从 MySQL 加载已处理文件: {} 条", processedPaths.size());
        } catch (Exception e) {
            log.error("从 MySQL 加载失败，缓存为空: {}", e.getMessage());
            return;
        }

        // 3. 回灌 Redis（分批）
        if (!processedPaths.isEmpty()) {
            backfillRedis(PROCESSED_KEY, processedPaths);
        }
    }

    /**
     * 判断文件是否已处理（纯内存，不走网络）
     */
    public boolean isProcessed(File file) {
        return processedPaths.contains(file.getAbsolutePath());
    }

    /** 按路径判断（Monitor 快速预过滤用） */
    public boolean isProcessed(String absolutePath) {
        return processedPaths.contains(absolutePath);
    }

    /**
     * 标记文件已处理：本地 + Redis
     */
    public void markProcessed(File file) {
        String path = file.getAbsolutePath();
        processedPaths.add(path);
        try {
            redisTemplate.opsForSet().add(PROCESSED_KEY, path);
        } catch (Exception e) {
            log.warn("Redis markProcessed 失败: {}", path, e.getMessage());
        }
    }

    // ==================== 全量文件清单缓存 ====================

    /**
     * Redis 中是否已有全量文件清单（非首次启动）
     */
    public boolean hasFileList() {
        try {
            Long size = redisTemplate.opsForSet().size(ALL_PATHS_KEY);
            return size != null && size > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从 Redis 拉取全量文件路径清单
     */
    public Set<String> getAllFilePaths() {
        try {
            Set<String> paths = redisTemplate.opsForSet().members(ALL_PATHS_KEY);
            if (paths != null && !paths.isEmpty()) {
                log.info("从 Redis 加载全量文件清单: {} 条", paths.size());
                return paths;
            }
        } catch (Exception e) {
            log.warn("Redis 加载全量文件清单失败: {}", e.getMessage());
        }
        return new HashSet<>();
    }

    /**
     * 首次扫描后批量写入全量文件清单到 Redis
     */
    public void storeFilePaths(Set<String> paths) {
        if (paths.isEmpty()) return;
        backfillRedis(ALL_PATHS_KEY, paths);
    }

    /**
     * WatchService 发现新文件时追加到 Redis 全量清单
     */
    public void addFilePath(String path) {
        try {
            redisTemplate.opsForSet().add(ALL_PATHS_KEY, path);
        } catch (Exception e) {
            log.warn("Redis addFilePath 失败: {}", path, e.getMessage());
        }
    }

    // ==================== 内部方法 ====================

    /**
     * 分批写入 Redis SET
     */
    private void backfillRedis(String key, Set<String> paths) {
        List<String> list = new ArrayList<>(paths);
        int total = list.size();
        int batches = (total + BATCH_SIZE - 1) / BATCH_SIZE;

        try {
            for (int i = 0; i < batches; i++) {
                int from = i * BATCH_SIZE;
                int to = Math.min(from + BATCH_SIZE, total);
                String[] chunk = list.subList(from, to).toArray(new String[0]);
                redisTemplate.opsForSet().add(key, chunk);
            }
            log.info("Redis 回灌完成: key={}, 总数={}, 批次={}", key, total, batches);
        } catch (Exception e) {
            log.warn("Redis 回灌失败: key={}, {}", key, e.getMessage());
        }
    }
}
