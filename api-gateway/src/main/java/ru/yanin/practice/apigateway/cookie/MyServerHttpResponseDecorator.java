package ru.yanin.practice.apigateway.cookie;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yanin.practice.apigateway.token.CookieName;

import java.nio.charset.StandardCharsets;

@Slf4j
public class MyServerHttpResponseDecorator extends ServerHttpResponseDecorator {

    private final DataBufferFactory bufferFactory = new DefaultDataBufferFactory();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MyServerHttpResponseDecorator(ServerHttpResponse delegate) {
        super(delegate);
    }

    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        if (getStatusCode() == HttpStatus.OK &&
                getHeaders().getContentType() != null &&
                getHeaders().getContentType().includes(MediaType.APPLICATION_JSON)) {

            return Flux.from(body)
                    .collectList()
                    .flatMap(dataBuffers -> {
                        DataBuffer combined = bufferFactory.allocateBuffer(10000);
                        for (var buffer : dataBuffers) {
                            combined.write(buffer);
                            DataBufferUtils.release(buffer);
                        }

                        byte[] bytes = new byte[combined.readableByteCount()];
                        combined.read(bytes);
                        DataBufferUtils.release(combined);

                        String responseBody = new String(bytes, StandardCharsets.UTF_8);

                        try {
                            String tokenString = extractToken(responseBody);
                            if (StringUtils.hasText(tokenString)) {
                                var cookie = ResponseCookie.from(
                                                CookieName.HOST_AUTH_TOKEN.getName(), tokenString)
                                        .path("/")
                                        .secure(true)
                                        .httpOnly(true)
                                        .maxAge(86400)
                                        .build();
                                addCookie(cookie);
                                log.info("Auth cookie added");
                            }
                        } catch (Exception e) {
                            log.error("Error processing token", e);
                        }
                        return Mono.just(bufferFactory.wrap(bytes));
                    })
                    .flatMap(buffer -> super.writeWith(Mono.just(buffer)));
        }
        return super.writeWith(body);
    }

    private String extractToken(String jsonBody) {
        try {
            JsonNode root = objectMapper.readTree(jsonBody);
            String tokenField = "token";
            if (root.has(tokenField)) {
                String token = root.get(tokenField).asText();
                if (StringUtils.hasText(token)) {
                    return token;
                }
            }
            return null;
        } catch (Exception e) {
            log.warn("Failed to extract token from JSON", e);
            return null;
        }
    }
}
