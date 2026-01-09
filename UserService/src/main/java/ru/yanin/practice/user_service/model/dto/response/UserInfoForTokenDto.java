package ru.yanin.practice.user_service.model.dto.response;

import ru.yanin.practice.user_service.model.entity.Role;

public record UserInfoForTokenDto(
        String username,
        Role role,
        Long id
) {
}
