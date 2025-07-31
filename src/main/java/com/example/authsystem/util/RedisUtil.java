package com.example.authsystem.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    public void set(String key, String value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }
    
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }
    
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }
    
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }
    
    public void increment(String key, long delta) {
        redisTemplate.opsForValue().increment(key, delta);
    }
    
    public void expire(String key, long timeout, TimeUnit unit) {
        redisTemplate.expire(key, timeout, unit);
    }
}