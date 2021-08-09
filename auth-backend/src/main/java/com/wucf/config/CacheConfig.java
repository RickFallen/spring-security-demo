package com.wucf.config;

import com.wucf.utils.cache.CacheUtil;
import com.wucf.utils.cache.CaffeineCacheUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

    @Bean
    public CacheUtil getCacheUtil(){
        return new CaffeineCacheUtil();
    }
}
