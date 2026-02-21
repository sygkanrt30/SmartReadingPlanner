package ru.yanin.practise.bookservice.service.user_api.internal;

import ru.yanin.shared.language.Language;

public interface InternalUserApiService {

    Language getLanguage(Long userId);
}
