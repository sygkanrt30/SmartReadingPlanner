package ru.yanin.shared.language;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum Language {
    ENGLISH("en", "eng"),
    RUSSIAN("ru", "rus");

    private final String[] reductions;

    Language(String... strings) {
        reductions = strings;
    }

    @JsonCreator
    public static Language fromString(String string) {
        for (var lang : Language.values()) {
            for (String reduction : lang.reductions) {
                if (reduction.equalsIgnoreCase(string)) {
                    return lang;
                }
            }
        }
        throw new IllegalArgumentException("Language not found: " + string);
    }
}
