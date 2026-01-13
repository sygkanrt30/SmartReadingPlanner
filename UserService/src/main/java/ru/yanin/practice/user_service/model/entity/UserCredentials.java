package ru.yanin.practice.user_service.model.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.yanin.practice.token.Role;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Table("users_credentials")
public record UserCredentials(
        @Id
        Long id,

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
        Instant createdAt

) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
