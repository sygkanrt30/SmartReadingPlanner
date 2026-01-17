package ru.yanin.practice.user_service.controller.external;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yanin.practice.user_service.service.user.user_settings.UserSettingsService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/user-setting")
class UserSettingController {

    private final UserSettingsService userSettingsService;

    @PatchMapping("change-status/email-notification")
    public ResponseEntity<?> changeStatusEmailNotification(@RequestParam("email") boolean isNeedEmailNotification,
                                                        @RequestHeader("X-User-ID") Long userId) {

        userSettingsService.changeStatusEmailNotification(userId, isNeedEmailNotification);
        return switch (isNeedEmailNotification) {
            case true -> ResponseEntity.ok("Email Notification on");
            case false -> ResponseEntity.ok("Email Notification off");
        };
    }

    @PatchMapping("change-status/telegram-notification")
    public ResponseEntity<?> changeStatusTelegramNotification(@RequestParam("telegram") boolean isNeedTgNotification,
                                                 @RequestHeader("X-User-ID") Long userId) {

        userSettingsService.changeStatusTgNotification(userId, isNeedTgNotification);
        return switch (isNeedTgNotification) {
            case true -> ResponseEntity.ok("Telegram Notification on");
            case false -> ResponseEntity.ok("Telegram Notification off");
        };
    }

    @PatchMapping("change-status/reading reminder")
    public ResponseEntity<?> changeStatusReadingReminder(@RequestParam("email") boolean isNeedReadingReminder,
                                              @RequestHeader("X-User-ID") Long userId) {

        userSettingsService.changeStatusReadingReminder(userId, isNeedReadingReminder);
        return switch (isNeedReadingReminder) {
            case true -> ResponseEntity.ok("Reading reminder on");
            case false -> ResponseEntity.ok("Reading reminder off");
        };
    }

    @PatchMapping("change-status/weekly-report")
    public ResponseEntity<?> changeStatusWeeklyReport(@RequestParam("email") boolean isNeedWeeklyReport,
                                              @RequestHeader("X-User-ID") Long userId) {

        userSettingsService.changeStatusWeeklyReport(userId, isNeedWeeklyReport);
        return switch (isNeedWeeklyReport) {
            case true -> ResponseEntity.ok("Weekly Report on");
            case false -> ResponseEntity.ok("Weekly Report off");
        };
    }
}
