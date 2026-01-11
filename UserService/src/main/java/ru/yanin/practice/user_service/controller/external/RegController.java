package ru.yanin.practice.user_service.controller.external;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yanin.practice.user_service.model.dto.request.UserCoreForReg;
import ru.yanin.practice.user_service.model.dto.response.RegResponse;
import ru.yanin.practice.user_service.service.highlevel.registration.RegistrationService;


@RestController
@RequestMapping("/api/v1/users/auth")
@RequiredArgsConstructor
@Validated
@Slf4j
class RegController {

    private final RegistrationService registrationService;

    @PostMapping("/reg")
    public ResponseEntity<RegResponse> doReg(@RequestBody @Valid UserCoreForReg userCoreForReg) {
        RegResponse response = registrationService.register(userCoreForReg);
        return ResponseEntity.ok(response);
    }
}
