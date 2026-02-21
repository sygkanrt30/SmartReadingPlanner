package ru.yanin.practice.user_service.controller.external;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yanin.practice.user_service.model.dto.request.user.core.FirstAndLastNameDto;
import ru.yanin.practice.user_service.service.user.core.UserCoreService;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("${spring.application.base-url}/user-core")
class UserCoreController {

    private final UserCoreService userCoreService;

    @PatchMapping("/change/fullName")
    public ResponseEntity<?> changeFullName(
            @RequestBody @Valid FirstAndLastNameDto dto,
            @RequestHeader("X-User-ID") Long userId) {

        userCoreService.changeFullName(dto, userId);
        return ResponseEntity.ok().build();
    }
}
