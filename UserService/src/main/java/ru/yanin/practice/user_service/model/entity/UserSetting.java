package ru.yanin.practice.user_service.model.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import ru.yanin.shared.language.Language;

import java.time.Instant;

@Table("user_settings")
public record UserSetting(
        @Id
        Long id,

        @Column("user_id")
        Long userId,

        @Column("email_notifications")
        boolean isNeedEmailNotifications,

        @Column("telegram_notifications")
        boolean isNeedTgNotifications,

        @Column("weekly_report")
        boolean isNeedWeeklyReport,

        @Column("reading_reminders")
        boolean isNeedReadingReminder,

        @Column("privacy_level")
        UserPrivacyLevel privacyLevel,

        @Column("language")
        Language lang,

        @CreatedDate
        @Column("created_at")
        Instant createdAt
) {
}
