package com.aerotickets.config;

import com.aerotickets.constants.CacheConstants;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean
    public Caffeine<Object, Object> caffeineConfig() {
        return Caffeine.newBuilder()
                .expireAfterWrite(CacheConstants.LIVE_FLIGHTS_EXPIRE_AFTER_WRITE_MINUTES, TimeUnit.MINUTES)
                .maximumSize(CacheConstants.LIVE_FLIGHTS_MAXIMUM_SIZE);
    }

    @Bean
    public CacheManager cacheManager(Caffeine<Object, Object> caffeine) {
        CaffeineCacheManager manager = new CaffeineCacheManager(CacheConstants.LIVE_FLIGHTS_CACHE_NAME);
        manager.setCaffeine(caffeine);
        return manager;
    }
}