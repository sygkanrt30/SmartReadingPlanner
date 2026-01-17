package ru.yanin.practice.user_service.controller.external;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yanin.practice.user_service.model.dto.request.auth.UserCoreForReg;
import ru.yanin.practice.user_service.model.dto.request.auth.UserCredentialsForAuth;
import ru.yanin.practice.user_service.service.auth.Authenticator;
import ru.yanin.practice.user_service.service.user.UserSavingService;


@RestController
@RequestMapping("/api/v1/users/auth")
@RequiredArgsConstructor
@Validated
@Slf4j
class AuthController {

    private final UserSavingService userService;
    private final Authenticator authenticator;

    @PostMapping("/reg")
    public ResponseEntity<String> doReg(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody @Valid UserCoreForReg userCoreForReg) {

        userService.save(userCoreForReg);
        authenticator.authenticateAndSetCookie(
                request, response,
                userCoreForReg.userCredentials().username(),
                userCoreForReg.userCredentials().password().getBytes());
        return ResponseEntity.ok("Registration Successful");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody @Valid UserCredentialsForAuth userCredentials) {

        log.info("Login attempt for user: {}", userCredentials.username());
        authenticator.authenticateAndSetCookie(request, response, userCredentials.username(),
                userCredentials.password().getBytes()
        );
        return ResponseEntity.ok("Log in successful");
    }
}
