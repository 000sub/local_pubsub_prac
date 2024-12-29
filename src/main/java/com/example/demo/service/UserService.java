package com.example.demo.service;

import com.example.demo.domain.User;
import com.example.demo.messaging.RedisMessagePublisher;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RedisMessagePublisher redisMessagePublisher;

    @Value("${app.redis.channel:userInvalidateChannel}")
    private String channelName;

    // 캐시를 사용하지 않고 DB에서 바로 조회
    public User getUserFromDb(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    /**
     * Local Cache에서 먼저 조회하고, 없으면 DB에서 조회 (Cacheable).
     * CacheManager는 CacheConfig에서 localCacheManager 를 지정해야 한다.
     */
    @Cacheable(cacheNames = "userLocalCache", key = "#id", cacheManager = "localCacheManager")
    public User getUserFromLocalCache(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    /**
     * Redis Cache에서 먼저 조회하고, 없으면 DB에서 조회 (Cacheable).
     * CacheManager는 CacheConfig에서 redisCacheManager 를 지정해야 한다.
     */
    @Cacheable(cacheNames = "userGlobalCache", key = "#id", cacheManager = "redisCacheManager")
    public User getUserFromRedisCache(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    /**
     * User Insert or Update 후 local cache 무효화를 위해 redis pub/sub 발행
     */
    @Transactional
    public User saveOrUpdateUser(User user) {
        User saved = userRepository.save(user);

        // userId를 Redis Pub/Sub으로 발행 -> 다른 서버 인스턴스의 local cache 무효화
        redisMessagePublisher.publish(channelName, saved.getId());

        return saved;
    }

    /**
     * 삭제 시 Local / Redis Cache 모두 무효화 예시
     */
    @Transactional
    @CacheEvict(cacheNames = {"userLocalCache", "userGlobalCache"}, key = "#id",
            cacheManager = "localCacheManager") // localCache도 Evict
    public void deleteUser(Long id) {
        userRepository.deleteById(id);

        // Redis Cache도 무효화
        // 단순히 redisCacheManager에서 @CacheEvict로도 처리 가능
        // 혹은 redisTemplate을 사용해 직접 제거도 가능
    }
}
