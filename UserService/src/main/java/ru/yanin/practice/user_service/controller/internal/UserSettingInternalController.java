package ru.yanin.practice.user_service.controller.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yanin.practice.user_service.service.user.user_settings.UserSettingsService;
import ru.yanin.shared.language.Language;

@RestController
@RequiredArgsConstructor
@RequestMapping("${spring.application.base-url}/internal/user-setting")
public class UserSettingInternalController {

    private final UserSettingsService userSettingsService;

    @GetMapping("/lang")
    public ResponseEntity<?> getLanguage(@RequestHeader("X-User-ID") Long userId) {
        Language lang = userSettingsService.getLangByUserId(userId);
        return ResponseEntity.ok(lang.name());
    }
}
