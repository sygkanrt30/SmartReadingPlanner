package ru.yanin.practise.bookservice.service.user_api.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.yanin.shared.header.HeaderName;
import ru.yanin.shared.language.Language;

@Service
@RequiredArgsConstructor
@Slf4j
public class InternalUserApiServiceImpl implements InternalUserApiService {

    private final RestClient userServiceApi;

    @Override
    public Language getLanguage(Long userId) {
        log.trace("get language by internal user service api for {}", userId);
        return userServiceApi.get()
                .uri(uriBuilder -> uriBuilder.path("/user-setting")
                        .build())
                .header(HeaderName.USER_ID.value(), String.valueOf(userId))
                .retrieve()
                .body(Language.class);
    }
}
