package com.example.mygrowth.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisConfig {
    /**
     * Redis 연결 설정
     * Localhost:6379 기준
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory("localhost", 6379);
    }

    /**
     * RefreshToken 전용 RedisTemplate
     */
    @Bean(name="refreshTokenRedisTemplate")
    public StringRedisTemplate refreshTokenRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return new StringRedisTemplate(redisConnectionFactory);
    }

    /**
     * 블랙리스트 토큰 (Access Token) 전용 RedisTemplate
     */
    @Bean(name="blacklistTokenRedisTemplate")
    public StringRedisTemplate blacklistRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return new StringRedisTemplate(redisConnectionFactory);
    }
}
