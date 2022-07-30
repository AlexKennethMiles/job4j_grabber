package ru.job4j.cache;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractCache<K, V> {

    protected final Map<K, SoftReference<V>> cache = new HashMap<>();

    public void put(K key, V value) {
        cache.put(key, new SoftReference<V>(value));
    }

    public V get(K key) {
        V object = cache.getOrDefault(key, new SoftReference<>(null)).get();
        if (object == null) {
            object = this.load(key);
            put(key, object);
        }
        return object;
    }

    protected abstract V load(K key);

}
