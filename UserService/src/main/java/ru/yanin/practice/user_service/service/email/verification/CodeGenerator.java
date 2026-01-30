package ru.yanin.practice.user_service.service.email.verification;

import java.util.Random;

final class CodeGenerator {

    static String generateCode() {
        var random = new Random();
        int firstCodePart = 100 + random.nextInt(899);
        int secondCodePart = 100 + random.nextInt(899);
        return String.format("%d%d", firstCodePart, secondCodePart);
    }
}
