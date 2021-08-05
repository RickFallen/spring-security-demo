package com.wucf.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存类
 * //TODO 用redis或者Caffeine
 */
public class CacheUtils {
    public static final Map<String, Object> CACHE = new ConcurrentHashMap<>();


    public static void put(String cacheName, String cacheKey, Object value) {
        CACHE.put(cacheKey, value);
    }

    public static Object get(String cacheName, String cacheKey) {
        return CACHE.get(cacheKey);
    }

    public static void remove(String cacheName, String cacheKey) {
        CACHE.remove(cacheKey);
    }

    public static void removeAll(String cacheName) {
        CACHE.clear();
    }
}
