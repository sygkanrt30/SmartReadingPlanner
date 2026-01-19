package ru.yanin.practice.user_service.model.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.List;

@Table("reading_profiles")
public record ReadingProfile(
        @Id
        Long id,

        @Column("user_id")
        Long userId,

        @Column("reading_speed")
        int wordsPerMin,

        @Column("daily_goal")
        int minPerDay,

        @Column("month_goal")
        int bookPerMonth,

        String timezone,

        @Column("preferred_genres")
        List<Genre> preferredGenres,

        @CreatedDate
        @Column("created_at")
        Instant createdAt
) {
}
