package com.example.mygrowth.domain.auth.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class BlacklistService {
    private final StringRedisTemplate redisTemplate;

    public BlacklistService(@Qualifier("blacklistTokenRedisTemplate")StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void blacklistToken(String token, long expirationMillis){
        // key: blacklist:token, value : "blacklisted"
//        if (expirationMillis <= 0) {
//            // 이미 만료된 토큰이면 블랙리스트에 넣을 필요 없음
//            return;
//        }
        redisTemplate.opsForValue().set(getKey(token), "blacklisted" , Duration.ofMillis(expirationMillis));
    }

    public boolean isBlacklisted(String token){
        return redisTemplate.hasKey(getKey(token));
    }

    private String getKey(String token){
        return "blacklisted:" + token;
    }

}
