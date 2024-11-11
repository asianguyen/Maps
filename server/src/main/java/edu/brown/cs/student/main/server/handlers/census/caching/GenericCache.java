package edu.brown.cs.student.main.server.handlers.census.caching;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.TimeUnit;

/**
 * A very generic cache implementation which can be specified in terms of a maximum time for an item
 * in the cache, and a maximum number of elements to be held in the cache at one time.
 *
 * @param <K> is the type of the key for the cache object.
 * @param <V> is the type of the value in the cache object.
 */
public class GenericCache<K, V> {
  private final Cache<K, V> cache; // An instance of a Guava cache.

  /**
   * The constructor initializes the cache with the specified parameters for max size and or max
   * time in the cache.
   *
   * @param sizeCap is a boolean for if the size is capped.
   * @param size is the size cap.
   * @param timeCap is a boolean for if the time is capped.
   * @param seconds is the time cap in number of seconds.
   */
  public GenericCache(boolean sizeCap, int size, boolean timeCap, int seconds) {
    if (sizeCap && timeCap) { // Size and time both specified.
      this.cache =
          CacheBuilder.newBuilder()
              .maximumSize(size)
              .expireAfterAccess(seconds, TimeUnit.SECONDS)
              .build();
    } else if (sizeCap) { // Only size specified.
      this.cache = CacheBuilder.newBuilder().maximumSize(size).build();
    } else if (timeCap) { // Only time specified.
      this.cache = CacheBuilder.newBuilder().expireAfterAccess(seconds, TimeUnit.SECONDS).build();
    } else { // Neither specified.
      this.cache = CacheBuilder.newBuilder().build();
    }
  }

  /**
   * Add a new value to the cache. Both of the generic type specified in the constructor.
   *
   * @param key is the key to access the value.
   * @param value is the value.
   */
  public void put(K key, V value) {
    cache.put(key, value);
  }

  /**
   * Retrieves the value associated with the given key.
   *
   * @param key is the key to look for.
   * @return the value if present, null if not.
   */
  public V get(K key) {
    return cache.getIfPresent(key);
  }
}
