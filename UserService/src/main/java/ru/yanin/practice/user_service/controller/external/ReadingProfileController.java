package ru.yanin.practice.user_service.controller.external;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yanin.practice.user_service.model.dto.request.user.reading_profle.UpdateReadingGoalsRequest;
import ru.yanin.practice.user_service.service.user.reading_profile.ReadingProfileService;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/reading-profile")
@Validated
class ReadingProfileController {

    private final ReadingProfileService readingProfileService;


    @PatchMapping("/add-favorite-genre")
    public ResponseEntity<?> addGenres(
            @RequestBody Set<String> genres,
            @RequestHeader("X-User-ID") Long userId
    ) {

        readingProfileService.addGenres(genres, userId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/remove-favorite-genre")
    public ResponseEntity<?> removeGenres(
            @RequestBody Set<String> genres,
            @RequestHeader("X-User-ID") Long userId
    ) {

        readingProfileService.removeGenres(genres, userId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/update-reading-goals")
    public ResponseEntity<?> updateReadingGoals(
            @RequestBody @Valid UpdateReadingGoalsRequest request,
            @RequestHeader("X-User-ID") Long userId) {

        readingProfileService.updateReadingGoals(userId, request);
        return ResponseEntity.ok(buildResponse(request));
    }

    private Map<String, Object> buildResponse(UpdateReadingGoalsRequest request) {
        var response = new HashMap<String, Object>();

        if (Objects.nonNull(request.wordsPerMin())) {
            response.put("reading_speed", request.wordsPerMin() + " слов/мин");
        }
        if (Objects.nonNull(request.minPerDay())) {
            response.put("daily_goal", request.minPerDay() + " мин/день");
        }
        if (Objects.nonNull(request.bookPerMonth())) {
            response.put("month_goal", request.bookPerMonth() + " книг/месяц");
        }

        if (response.isEmpty()) {
            response.put("message", "No reading goals were updated");
        } else {
            response.put("message", "Reading goals updated successfully");
        }

        return response;
    }
}
