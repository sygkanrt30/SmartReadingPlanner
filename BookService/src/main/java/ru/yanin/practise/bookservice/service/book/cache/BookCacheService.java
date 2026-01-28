package ru.yanin.practise.bookservice.service.book.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import ru.yanin.practise.bookservice.model.entity.Book;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class BookCacheService implements CacheService<String, Book> {

    private final static String KEY_PREFIX = "cache:book:";

    private final RedisTemplate<String, Book> redisTemplate;
    private final long ttlInSec;

    public BookCacheService(RedisTemplate<String, Book> redisTemplate,
                            @Value("${spring.data.redis.book.ttl}") long ttlInSec) {
        this.redisTemplate = redisTemplate;
        this.ttlInSec = ttlInSec;
    }

    @Override
    public Optional<Book> get(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue()
                .get(KEY_PREFIX + key));
    }

    @Override
    public void evict(String key) {
        redisTemplate.delete(KEY_PREFIX + key);
        log.trace("evict key: {}", key);
    }

    @Override
    public void cache(String key, Book value) {
        String fullKey = KEY_PREFIX + key;
        redisTemplate.opsForValue().set(fullKey, value,
                ttlInSec, TimeUnit.SECONDS);
        log.debug("cache book: {}", value);
    }
}
