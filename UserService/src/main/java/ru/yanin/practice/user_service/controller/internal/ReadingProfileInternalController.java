package ru.yanin.practice.user_service.controller.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yanin.practice.user_service.service.user.reading_profile.ReadingProfileService;
import ru.yanin.shared.genre.Genre;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("${spring.application.base-url}/internal/reading-profile")
public class ReadingProfileInternalController {

    private final ReadingProfileService readingProfileService;

    @GetMapping("/genres")
    public ResponseEntity<Set<Genre>> getLanguage(@RequestHeader("X-User-ID") Long userId) {
        Set<Genre> genres = readingProfileService.getGenresById(userId);
        return ResponseEntity.ok(genres);
    }
}
