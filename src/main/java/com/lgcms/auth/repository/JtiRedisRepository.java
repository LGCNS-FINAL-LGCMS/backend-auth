package com.lgcms.auth.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class JtiRedisRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private final String keyPrefix = "jti:";
    private final String value = "revoked";
    private @Value("${jwt.refresh_expired_time}") Long refreshExpiredTime;

    public boolean addJti(String jti) {
        redisTemplate.opsForValue().set(keyPrefix + jti, value, Duration.ofMillis(refreshExpiredTime));
        return true;
    }

    public boolean isBlackList(String jti) {
        return redisTemplate.opsForValue().get(keyPrefix + jti) != null;
    }
}
