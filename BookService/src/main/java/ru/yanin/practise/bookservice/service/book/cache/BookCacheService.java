package ru.yanin.practise.bookservice.service.book.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import ru.yanin.practise.bookservice.model.dto.BookDto;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class BookCacheService implements CacheService<String, BookDto> {

    private final static String KEY_PREFIX = "cache:book:";

    private final RedisTemplate<String, BookDto> redisTemplate;
    private final long ttlInSec;

    public BookCacheService(RedisTemplate<String, BookDto> redisTemplate,
                            @Value("${spring.data.redis.book.ttl}") long ttlInSec) {
        this.redisTemplate = redisTemplate;
        this.ttlInSec = ttlInSec;
    }

    @Override
    public Optional<BookDto> get(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue()
                .get(KEY_PREFIX + key));
    }

    @Override
    public void evict(String key) {
        redisTemplate.delete(KEY_PREFIX + key);
        log.trace("evict key: {}", key);
    }

    @Override
    public void cache(String key, BookDto value) {
        String fullKey = KEY_PREFIX + key;
        redisTemplate.opsForValue().set(fullKey, value,
                ttlInSec, TimeUnit.SECONDS);
        log.debug("cache book: {}", value);
    }
}
