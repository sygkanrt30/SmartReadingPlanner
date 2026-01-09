package ru.yanin.practice.user_service.model.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import ru.yanin.practice.token.Role;

import java.time.Instant;
import java.util.UUID;

@Table("users_credentials")
public record UserCredentials(
        @Id
        @Column("user_id")
        Long userId,

        String username,

        String password,

        String email,

        Role role,

        @Column("email_verified")
        boolean emailVerified,

        @CreatedDate
        @Column("created_at")
        Instant createdAt,

        @Column("current_token_id")
        UUID currentTokenId
) {
}
