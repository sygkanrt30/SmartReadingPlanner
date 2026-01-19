package ru.yanin.practice.user_service.service.user.reading_profile;

import ru.yanin.practice.user_service.model.dto.request.auth.UserCoreForReg;
import ru.yanin.practice.user_service.model.dto.request.user.reading_profle.UpdateReadingGoalsRequest;

import java.util.Set;

public interface ReadingProfileService {

    void save(UserCoreForReg userCoreForReg, Long savedUserId);

    void updateReadingGoals(Long userId, UpdateReadingGoalsRequest request);

    void addGenres(Set<String> genres, Long userId);

    void removeGenres(Set<String> genres, Long userId);
}
