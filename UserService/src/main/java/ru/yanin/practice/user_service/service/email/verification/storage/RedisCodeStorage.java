package ru.yanin.practice.user_service.service.email.verification.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
@Slf4j
public class RedisCodeStorage implements CodeStorage {

    private static final StringBuilder KEY_BUILDER = new StringBuilder("email:verification:");

    private final long keyExpirationTimeout;
    private final long blockTimeout;
    private final StringRedisTemplate redisTemplate;
    private final ValueOperations<String, String> valueOperations;

    public RedisCodeStorage(
            @Value("${spring.data.redis.storage.email-code.expiration.sec.key}") long keyExpirationTimeout,
            @Value("${spring.data.redis.storage.email-code.expiration.sec.block}") long sendCodeBlockTimeout,
            StringRedisTemplate redisTemplate) {

        this.keyExpirationTimeout = keyExpirationTimeout;
        this.redisTemplate = redisTemplate;
        this.valueOperations = redisTemplate.opsForValue();
        this.blockTimeout = sendCodeBlockTimeout;
    }

    @Override
    public void saveCode(String email, String code) {
        String key = KEY_BUILDER.append(email).toString();
        valueOperations.set(key, code, Duration.ofSeconds(keyExpirationTimeout));
        log.debug("Code saved for key: {}", key);
    }

    @Override
    public Optional<String> getCode(String email) {
        return Optional.ofNullable(valueOperations.get(KEY_BUILDER.append(email).toString()));
    }

    @Override
    public void deleteCode(String email) {
        redisTemplate.delete(KEY_BUILDER.append(email).toString());
        log.trace("Code deleted for key: {}", KEY_BUILDER.append(email));
    }

    @Override
    public boolean isBlocked(String email) {
        String key =  KEY_BUILDER.append("send-code:block:").append(email).toString();
        return redisTemplate.hasKey(key);
    }

    @Override
    public void blockForResend(String email) {
        String key =  KEY_BUILDER.append("block:").append(email).toString();
        valueOperations.set(key, "blocked", Duration.ofSeconds(blockTimeout));
        log.trace("Code blocked for resend for key: {}", key);
    }
}
