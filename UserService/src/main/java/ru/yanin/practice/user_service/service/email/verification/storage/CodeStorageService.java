package ru.yanin.practice.user_service.service.email.verification.storage;

import java.util.Optional;

public interface CodeStorageService {

    void saveCode(String email, String code);

    Optional<String> getCode(String email);

    void deleteCode(String email);
}
