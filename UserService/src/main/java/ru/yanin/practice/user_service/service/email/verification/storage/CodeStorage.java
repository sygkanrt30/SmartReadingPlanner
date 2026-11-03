package ru.yanin.practice.user_service.service.email.verification.storage;

import java.util.Optional;

public interface CodeStorage {

    void saveCode(String email, String code);

    Optional<String> getCode(String email);

    void deleteCode(String email);

    boolean isBlocked(String email);

    void blockForResend(String email);
}
