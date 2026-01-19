package ru.yanin.practice.user_service.controller.external.user_setting;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yanin.practice.user_service.service.user.user_settings.UserSettingsService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/user-setting")
class UserSettingController {

    private final UserSettingsService userSettingsService;

    @PatchMapping("/change-status")
    public ResponseEntity<?> changeNotificationStatus(@RequestParam("status") boolean notificationStatus,
                                                      @RequestParam("type_of_notification") String typeOfNotification,
                                                      @RequestHeader("X-User-ID") Long userId) {
        var typeOfNotificationEnum = TypeOfNotification.fromValue(typeOfNotification);
        switch (typeOfNotificationEnum) {
            case EMAIL -> userSettingsService.changeStatusEmailNotification(userId, notificationStatus);
            case TELEGRAM -> userSettingsService.changeStatusTgNotification(userId, notificationStatus);
            case WEEKLY_REPORT -> userSettingsService.changeStatusWeeklyReport(userId, notificationStatus);
            case READING_REMINDER -> userSettingsService.changeStatusReadingReminder(userId, notificationStatus);
        }
        return switch (notificationStatus) {
            case true -> ResponseEntity.ok(String.format("%s Notification on", typeOfNotificationEnum));
            case false -> ResponseEntity.ok(String.format("%s Notification off", typeOfNotificationEnum));
        };
    }

}
