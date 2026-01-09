package ru.yanin.practice.user_service.model.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("user_settings")
public record UserSetting(
        @Id
        Long id,

        @Column("user_id")
        Long userId,

        @Column("email_notifications")
        boolean isNeedEmailNotifications,

        @Column("push_notifications")
        boolean isNeedPushNotifications,

        @Column("weekly_report")
        boolean isNeedWeeklyReport,

        @Column("reading_reminders")
        boolean isNeedReadingReminder,

        @Column("privacy_level")
        UserPrivacyLevel privacyLevel,

        @Column("language")
        String lang,

        @CreatedDate
        @Column("created_at")
        Instant createdAt
) {
}
