package ru.yanin.practise.bookservice.model.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.yanin.shared.language.Language;

import java.util.Objects;

@Converter(autoApply = true)
public class LanguageConverter implements AttributeConverter<Language, String> {

    @Override
    public String convertToDatabaseColumn(Language language) {
        return Objects.isNull(language) ? null : language.getFirstReduction();
    }

    @Override
    public Language convertToEntityAttribute(String dbData) {
        return Objects.isNull(dbData) ? null : Language.fromString(dbData);
    }
}
