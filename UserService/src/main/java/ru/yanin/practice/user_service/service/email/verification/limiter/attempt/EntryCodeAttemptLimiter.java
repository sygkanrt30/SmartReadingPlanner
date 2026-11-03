package ru.yanin.practice.user_service.service.email.verification.limiter.attempt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;

@Slf4j
@Component
public class EntryCodeAttemptLimiter implements AttemptLimiter {

    private static final StringBuilder KEY_BUILDER = new StringBuilder("email:verify:attempt:");

    private final long keyExpiration;
    private final int attemptLimit;
    private final RedisTemplate<String, Integer> redisTemplate;
    private final ValueOperations<String, Integer> valueOps;

    public EntryCodeAttemptLimiter(
            @Value("${spring.data.redis.storage.attempt.expiration.sec.attempt-key}") long keyExpiration,
            @Value("${spring.data.redis.storage.attempt.limit}") int limit,
            RedisTemplate<String, Integer> redisTemplate) {

        this.keyExpiration = keyExpiration;
        this.redisTemplate = redisTemplate;
        this.attemptLimit = limit;
        this.valueOps = redisTemplate.opsForValue();
    }

    @Override
    public boolean isAttemptAllowed(String email) {
        return getAttemptCount(email) >= attemptLimit;
    }

    @Override
    public void registerFailedAttempt(String email) {
        String key = KEY_BUILDER.append(email).toString();
        valueOps.increment(key);

        if (getAttemptCount(email) == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(keyExpiration));
        }
    }

    @Override
    public void resetAttempts(String email) {
        String key = KEY_BUILDER.append(email).toString();
        valueOps.increment(key);
    }

    @Override
    public int getAttemptCount(String email) {
        String key = KEY_BUILDER.append(email).toString();
        Integer attempts = valueOps.get(key);
        return Objects.nonNull(attempts) ? attempts : 0;
    }
}
