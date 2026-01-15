package ru.yanin.practice.apigateway.ratelimit.util;

import org.instancio.Instancio;
import module org.junit.jupiter.api;
import org.junit.runner.RunWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import ru.yanin.practice.apigateway.config.RateLimitConfig;
import ru.yanin.practice.apigateway.token.TokenService;
import ru.yanin.practice.token.Token;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.instancio.Select.field;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@Testcontainers
@DataRedisTest
@RunWith(SpringRunner.class)
@ExtendWith(MockitoExtension.class)
public class RateLimitUtilTest {

    @SuppressWarnings("resource")
    @Container
    static GenericContainer<?> redis = new GenericContainer<>(
            DockerImageName.parse("redis:7.2-alpine"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.cache.type", () -> "redis");
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    private TokenService tokenService;
    private ReactiveStringRedisTemplate redisTemplate;

    @BeforeEach
    void setUp(@Autowired ReactiveStringRedisTemplate redisTemplate) {
        tokenService = mock(TokenService.class);
        this.redisTemplate = redisTemplate;
        redisTemplate.getConnectionFactory()
                .getReactiveConnection()
                .serverCommands()
                .flushAll()
                .block();
    }

    @Test
    void isExcluded_ShouldReturnFalse_WhenPathNotExistsInExcludedList() {
        ServerHttpRequest request = MockServerHttpRequest
                .method(HttpMethod.GET, "/test/not_excluded/path")
                .build();
        var config = new RateLimitConfig()
                .setExcludedPaths(List.of("/test/excluded/path"));
        var rateLimitUtil = new RateLimitUtil(config, redisTemplate, tokenService);

        boolean result = rateLimitUtil.isExcluded(request);

        Assertions.assertFalse(result);
    }

    @Test
    void isExcluded_ShouldReturnTrue_WhenPathExistsInExcludedList() {
        ServerHttpRequest request = MockServerHttpRequest
                .method(HttpMethod.GET, "/test/excluded/path")
                .build();
        var config = new RateLimitConfig()
                .setExcludedPaths(List.of("/test/excluded/path", "/test/path/3123123"));
        var rateLimitUtil = new RateLimitUtil(config, redisTemplate, tokenService);

        boolean result = rateLimitUtil.isExcluded(request);

        assertTrue(result);
    }

    @Test
    void getMaxRequest_ShouldReturnCustomLimit_WhenItExists() {
        ServerHttpRequest request = MockServerHttpRequest
                .method(HttpMethod.GET, "/test/mock/path")
                .build();
        var config = new RateLimitConfig()
                .setCustomLimits(Map.of("/test/mock/*", 100L));
        var rateLimitUtil = new RateLimitUtil(config, redisTemplate, tokenService);

        long result = rateLimitUtil.getMaxRequests(request);

        Assertions.assertEquals(100L, result);
    }

    @Test
    void getMaxRequest_ShouldReturnDefaultLimit_WhenItNotExists() {
        ServerHttpRequest request = MockServerHttpRequest
                .method(HttpMethod.GET, "/test/mock/path")
                .build();
        var config = new RateLimitConfig()
                .setCustomLimits(Map.of("/test/mock/path2", 123123L))
                .setDefaultMaxRequests(100L);
        var rateLimitUtil = new RateLimitUtil(config, redisTemplate, tokenService);

        long result = rateLimitUtil.getMaxRequests(request);

        Assertions.assertEquals(100L, result);
    }

    @Test
    void checkRateLimit_ShouldReturnTrue_WhenRequestDoesNotExceedLimit_WithDefaultConfig() {
        ServerHttpRequest request = MockServerHttpRequest
                .method(HttpMethod.POST, "/api/v1/users/add/something")
                .build();
        var config = new RateLimitConfig();
        var rateLimitUtil = new RateLimitUtil(config, redisTemplate, tokenService);

        String username = "user-123";
        Token token = Instancio.of(Token.class)
                .set(field(Token::username), username)
                .create();
        when(tokenService.extractToken(any()))
                .thenReturn("valid-token");
        when(tokenService.validateToken("valid-token"))
                .thenReturn(token);

        //Assert & Act
        for (int i = 0; i < 5; i++) {
            StepVerifier.create(rateLimitUtil.checkRateLimit(request))
                    .expectNext(true)
                    .verifyComplete();
        }
    }

    @Test
    void checkRateLimit_ShouldReturnFalse_WhenRequestDoesExceedLimit_WithCustomConfig() {
        ServerHttpRequest request = MockServerHttpRequest
                .method(HttpMethod.POST, "/api/v1/users/add/something")
                .build();
        var config = new RateLimitConfig()
                .setCustomLimits(Map.of("/api/v1/users/add/something", 3L));
        var rateLimitUtil = new RateLimitUtil(config, redisTemplate, tokenService);

        String username = "user-123";
        Token token = Instancio.of(Token.class)
                .set(field(Token::username), username)
                .create();
        when(tokenService.extractToken(any()))
                .thenReturn("valid-token");
        when(tokenService.validateToken("valid-token"))
                .thenReturn(token);

        // Act
        Flux<Boolean> rateLimitFlux = Flux.range(0, 5)
                .concatMap(_ -> rateLimitUtil.checkRateLimit(request));

        // Assert
        StepVerifier.create(rateLimitFlux)
                .expectNext(true, true, true, false, false)
                .verifyComplete();
    }

    @Test
    void checkRateLimit_ShouldReturnTrue_WhenAuthRequestDoesNotExceedLimit_WithDefaultConfig() {
        ServerHttpRequest request = MockServerHttpRequest
                .method(HttpMethod.POST, "/api/v1/users/auth/login")
                .remoteAddress(new InetSocketAddress("127.0.0.0", 12345))
                .build();
        var config = new RateLimitConfig();
        var rateLimitUtil = new RateLimitUtil(config, redisTemplate, tokenService);

        // Act
        Flux<Boolean> rateLimitFlux = Flux.range(0, 12)
                .concatMap(_ -> rateLimitUtil.checkRateLimit(request));

        // Assert
        StepVerifier.create(rateLimitFlux)
                .recordWith(ArrayList::new)
                .expectNextCount(12)
                .consumeRecordedWith(results -> {
                    assertEquals(12, results.size());
                    assertTrue(results.stream().allMatch(result -> result));
                })
                .verifyComplete();
    }

    @Test
    void checkRateLimit_ShouldReturnFalse_WhenAuthRequestDoesExceedLimit_WithCustomConfig() {
        ServerHttpRequest request = MockServerHttpRequest
                .method(HttpMethod.POST, "/api/v1/users/auth/reg")
                .remoteAddress(new InetSocketAddress("127.0.0.0", 12345))
                .build();
        var config = new RateLimitConfig()
                .setCustomLimits(Map.of("/api/v1/users/auth/reg", 2L));
        var rateLimitUtil = new RateLimitUtil(config, redisTemplate, tokenService);


        // Act
        Flux<Boolean> rateLimitFlux = Flux.range(0, 5)
                .concatMap(_ -> rateLimitUtil.checkRateLimit(request));

        // Assert
        StepVerifier.create(rateLimitFlux)
                .expectNext(true, true, false, false, false)
                .verifyComplete();
    }
}
