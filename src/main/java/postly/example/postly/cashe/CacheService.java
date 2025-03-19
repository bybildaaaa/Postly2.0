package postly.example.postly.cashe;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class CacheService<K, V> {
    private final int MaxCacheSize = 3;
    private final Map<K, V> cache;

    public CacheService() {
        this.cache = new LinkedHashMap<>(MaxCacheSize, 0.75f, true) {
            @Override
      protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > MaxCacheSize;
            }
        };
    }

    public synchronized V get(K key) {
        return cache.get(key);
    }

    public synchronized void put(K key, V value) {
        cache.put(key, value);
    }

    public synchronized void remove(K key) {
        cache.remove(key);
    }

    public synchronized void clear() {
        cache.clear();
    }

    public synchronized Map<K, V> getCacheContents() {
        return new LinkedHashMap<>(cache);  // Возвращаем копию текущего состояния кэша
    }
}
