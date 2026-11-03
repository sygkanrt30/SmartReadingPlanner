package ru.yanin.practise.bookservice.service.book.cache;

import java.util.Optional;

public interface CacheService<K, V> {

    Optional<V> get(K key);

    void evict(K key);

    void cache(K key, V value);

}
