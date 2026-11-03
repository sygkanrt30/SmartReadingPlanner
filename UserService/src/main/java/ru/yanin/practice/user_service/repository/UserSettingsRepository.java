package ru.yanin.practice.user_service.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.yanin.practice.user_service.model.entity.UserSetting;

import java.util.Optional;

@Repository
public interface UserSettingsRepository extends CrudRepository<UserSetting, Long> {

    @Modifying
    @Query("UPDATE user_settings SET email_notifications = :isNeedEmailNotification WHERE user_id = :userId")
    void updateStatusEmailNotification(Long userId, boolean isNeedEmailNotification);

    @Modifying
    @Query("UPDATE user_settings SET telegram_notifications = :isNeedTgNotification WHERE user_id = :userId")
    void updateStatusTgNotification(Long userId, boolean isNeedTgNotification);

    @Modifying
    @Query("UPDATE user_settings SET reading_reminders = :isNeedReadingReminder WHERE user_id = :userId")
    void updateStatusReadingReminder(Long userId, boolean isNeedReadingReminder);

    @Modifying
    @Query("UPDATE user_settings SET weekly_report = :isNeedWeeklyReport WHERE user_id = :userId")
    void updateStatusWeeklyReport(Long userId, boolean isNeedWeeklyReport);

    Optional<UserSetting> findByUserId(Long userId);
}
