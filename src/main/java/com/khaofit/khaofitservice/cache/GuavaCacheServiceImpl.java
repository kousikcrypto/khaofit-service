package com.khaofit.khaofitservice.cache;

import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * this is a guava cache service implementation .
 *
 * @author kousik manik
 */
@Service
public class GuavaCacheServiceImpl {


  @Autowired
  private LoadingCache<String, Object> guavaCache;

  /**
   * Puts a key-value pair into the cache.
   *
   * @param key   the key with which the specified value is to be associated
   * @param value the value to be associated with the specified key
   */
  public void put(String key, Object value) {
    guavaCache.put(key, value);
  }

  /**
   * Returns the value to which the specified key is mapped,
   * or {@code null} if this cache contains no mapping for the key.
   *
   * @param key the key whose associated value is to be returned
   * @return the value to which the specified key is mapped,
   *         or {@code null} if this cache contains no mapping for the key
   */
  public Object get(String key) {
    return guavaCache.getIfPresent(key);
  }

  /**
   * Removes the mapping for a key from this cache if it is present.
   *
   * @param key the key whose mapping is to be removed from the cache
   */
  public void remove(String key) {
    guavaCache.invalidate(key);
  }

}
