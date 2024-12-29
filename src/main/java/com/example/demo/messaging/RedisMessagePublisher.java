package com.example.demo.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisMessagePublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(String channel, Long userId) {
        redisTemplate.convertAndSend(channel, userId.toString());
    }
}
