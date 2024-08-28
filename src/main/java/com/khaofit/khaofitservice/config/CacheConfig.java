package com.khaofit.khaofitservice.config;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * cache configuration class .
 *
 * @author kousik && kuldeep .
 */
@Configuration
public class CacheConfig {

  /**
   * this is bean class for cache config class .
   *
   * @return @{@link LoadingCache}
   */
  @Bean
  public LoadingCache<String, Object> guavaCache() {
    return CacheBuilder.newBuilder()
        .maximumSize(100000)
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .build(new CacheLoader<String, Object>() {
          @NotNull
          @Override
          public Object load(@NotNull String key) throws Exception {
            // Define how to load the data, if not present in the cache
            return new Object(); // Default object, modify as per your needs
          }
        });
  }

}

