package ru.yanin.practice.user_service.model.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import ru.yanin.shared.genre.Genre;
import ru.yanin.practice.user_service.model.entity.UserPrivacyLevel;

import java.time.LocalDate;
import java.util.Set;

public record UserCoreForReg(
        UserCredentialsForReg userCredentials,

        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        LocalDate birthDate,

        UserPrivacyLevel privacyLevel,

        @NotBlank
        String timezone,

        int wordsPerMin,

        int minPerDay,

        int bookPerMonth,

        @NotEmpty
        Set<Genre> preferredGenres,

        boolean isNeedEmailNotifications,

        boolean isNeedTgNotifications,

        boolean isNeedWeeklyReport,

        boolean isNeedReadingReminder
) {
}
