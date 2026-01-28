package ru.yanin.practice.user_service.service.email.verification.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisCodeStorageService implements CodeStorageService {

    private static final String KEY_PREFIX = "email:verify:";

    @Value("${spring.data.redis.storage.email-code.key.expiration.sec}")
    private long keyExpirationTimeout;
    private final StringRedisTemplate redisTemplate;

    @Override
    public void saveCode(String email, String code) {
        String key = KEY_PREFIX + email;
        redisTemplate.opsForValue().set(key, code,
                keyExpirationTimeout, TimeUnit.SECONDS
        );
        log.debug("Code saved for key: {} on 60 sec", key);
    }

    @Override
    public Optional<String> getCode(String email) {
        return Optional.ofNullable(redisTemplate.opsForValue()
                .get(KEY_PREFIX + email));
    }

    @Override
    public void deleteCode(String email) {
        redisTemplate.delete(KEY_PREFIX + email);
        log.trace("Code deleted for key: {}", KEY_PREFIX + email);
    }
}
