package com.example.demo.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RedisMessageListener implements MessageListener {

    private final CacheManager cacheManager;

    public RedisMessageListener(@Qualifier("localCacheManager") CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String userIdStr = new String(message.getBody());
        log.info("Received message from Redis Pub/Sub: userId = {}", userIdStr);

        // localCacheManager의 userLocalCache 에서 해당 ID 삭제
        Cache userLocalCache = cacheManager.getCache("userLocalCache");
        if (userLocalCache == null) {
            log.error("User Cache Not Exist");
            return;
        }
        userLocalCache.evictIfPresent(Long.valueOf(userIdStr));
        log.info("Evicted userId {} from local cache", userIdStr);
    }
}
