package ru.yanin.practice.user_service.model.dto.request.user.reading_profle;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import ru.yanin.practice.user_service.model.entity.ReadingProfile;

import java.util.Objects;

public record UpdateReadingGoalsRequest(
        @Min(value = 10, message = "Reading speed must be at least 10 words per minute")
        @Max(value = 1000, message = "Reading speed cannot exceed 1000 words per minute")
        Integer wordsPerMin,

        @Min(value = 1, message = "Daily goal must be at least 1 minute")
        @Max(value = 480, message = "Daily goal cannot exceed 480 minutes (8 hours)")
        Integer minPerDay,

        @Min(value = 0, message = "Monthly goal cannot be negative")
        @Max(value = 50, message = "Monthly goal cannot exceed 50 books")
        Integer bookPerMonth
) {

    public boolean hasChanges(ReadingProfile currentProfile) {
        return !Objects.equals(wordsPerMin, currentProfile.wordsPerMin()) ||
                !Objects.equals(minPerDay, currentProfile.minPerDay()) ||
                !Objects.equals(bookPerMonth, currentProfile.bookPerMonth());
    }
}
