package ru.yanin.practice.user_service.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import ru.yanin.practice.user_service.model.entity.Genre;
import ru.yanin.practice.user_service.model.entity.UserPrivacyLevel;

import java.time.LocalDate;
import java.util.List;

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

        @NotEmpty
        List<Genre> preferredGenres,

        boolean isNeedEmailNotifications,

        boolean isNeedPushNotifications,

        boolean isNeedWeeklyReport,

        boolean isNeedReadingReminder
) {
}
