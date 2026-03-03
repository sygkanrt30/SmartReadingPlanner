package ru.yanin.practise.bookservice.service.user_api.internal;

import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.yanin.shared.genre.Genre;
import ru.yanin.shared.header.HeaderName;
import ru.yanin.shared.language.Language;

import java.util.Collections;
import java.util.Set;

@SuppressWarnings("unused")
@Service
@RequiredArgsConstructor
@Slf4j
public class InternalUserApiServiceImpl implements InternalUserApiService {

    private final RestClient userServiceApi;

    @Override
    @Retry(name = "${user-service.api.retry-name}", fallbackMethod = "langFallback")
    public Language getLanguage(Long userId) {
        log.trace("get languageCondition by internal user service api for {}", userId);
        String langSrt = userServiceApi.get()
                .uri(uriBuilder -> uriBuilder.path("/user-setting/lang")
                        .build())
                .header(HeaderName.USER_ID.value(), String.valueOf(userId))
                .retrieve()
                .body(String.class);
        return Language.valueOf(langSrt);
    }

    private Language langFallback(Long userId, Exception e) {
        log.warn("fallback method worked to get the user's languageCondition; userId={}", userId);
        return null;
    }

    @Override
    @Retry(name = "${user-service.api.retry-name}", fallbackMethod = "genreFallback")
    public @NotNull Set<Genre> getFavoriteGenre(Long userId) {
        log.trace("get favorite genre by internal user service api for {}", userId);
        return userServiceApi.get()
                .uri(uriBuilder -> uriBuilder.path("/reading-profile/genres")
                        .build())
                .header(HeaderName.USER_ID.value(), String.valueOf(userId))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    private Set<Genre> genreFallback(Long userId, Exception e) {
        log.warn("fallback method worked to get the user's genres; userId={}", userId);
        return Collections.emptySet();
    }
}
