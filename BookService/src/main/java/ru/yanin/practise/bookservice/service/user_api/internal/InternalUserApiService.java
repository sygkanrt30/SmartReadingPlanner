package ru.yanin.practise.bookservice.service.user_api.internal;

import ru.yanin.shared.genre.Genre;
import ru.yanin.shared.language.Language;

import java.util.Set;

public interface InternalUserApiService {

    Language getLanguage(Long userId);

    Set<Genre> getFavoriteGenre(Long userId);
}
