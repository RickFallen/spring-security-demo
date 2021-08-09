package com.wucf.utils.cache;

import com.github.benmanes.caffeine.cache.*;

import java.time.Duration;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class CaffeineCacheUtil implements CacheUtil {

    private final Cache<String, Object> CACHE;

    public CaffeineCacheUtil() {
        CACHE = Caffeine.newBuilder()
                .expireAfter(new Expiry<String, Object>() {
                    //默认过期时间是不过期
                    public long expireAfterCreate(String key, Object obj, long currentTime) {
                        Integer max = Integer.MAX_VALUE;
                        return TimeUnit.SECONDS.toNanos(max);
                    }

                    public long expireAfterUpdate(String key, Object obj,
                                                  long currentTime, long currentDuration) {
                        return currentDuration;
                    }

                    public long expireAfterRead(String key, Object obj,
                                                long currentTime, long currentDuration) {
                        return currentDuration;
                    }
                }).scheduler(Scheduler.systemScheduler()).build();
    }

    @Override
    public <T> void setCacheObject(String key, T value) {
        CACHE.put(key, value);
    }


    @Override
    public <T> void setCacheObject(String key, T value, Integer timeout, TimeUnit timeUnit) {
        Policy.VarExpiration<String, Object> stringObjectVarExpiration = CACHE.policy().expireVariably().get();
        stringObjectVarExpiration.put(key, value, timeout, timeUnit);
    }

    @Override
    public boolean expire(String key, long timeout, TimeUnit unit) {
        Policy.VarExpiration<String, Object> stringObjectVarExpiration = CACHE.policy().expireVariably().get();
        Optional<Duration> expiresAfter = stringObjectVarExpiration.getExpiresAfter(key);
        if(expiresAfter.isPresent()){
            Duration duration = expiresAfter.get();
            if(duration.getNano() > 0){
                stringObjectVarExpiration.setExpiresAfter(key, timeout, unit);
                return true;
            }
        }
        return false;
    }

    @Override
    public <T> T getCacheObject(String key) {
        T value = (T) CACHE.getIfPresent(key);
        return value;
    }

    @Override
    public boolean deleteObject(String key) {
        CACHE.invalidate(key);
        return true;
    }

    @Override
    public boolean deleteObject(Collection collection) {
        CACHE.invalidateAll(collection);
        return true;
    }
}
