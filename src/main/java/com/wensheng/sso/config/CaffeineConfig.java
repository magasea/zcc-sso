package com.wensheng.sso.config;

import com.github.benmanes.caffeine.cache.Ticker;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CaffeineConfig {
  @Bean
  public CacheManager cacheManager(Ticker ticker) {
    CaffeineCache token = buildCache("TOKEN", ticker, 10);
    CaffeineCache user = buildCache("USER", ticker, 10);


//    CaffeineCache notificationCache = buildCache("notifications", ticker, 60);
    SimpleCacheManager manager = new SimpleCacheManager();
    manager.setCaches(Arrays.asList(token, user));
    return manager;
  }

  private CaffeineCache buildCache(String name, Ticker ticker, int minutesToExpire) {
    return new CaffeineCache(name, Caffeine.newBuilder()
        .expireAfterWrite(minutesToExpire, TimeUnit.MINUTES)
        .maximumSize(100)
        .ticker(ticker)
        .build());
  }

  @Bean
  public Ticker ticker() {
    return Ticker.systemTicker();
  }
}
