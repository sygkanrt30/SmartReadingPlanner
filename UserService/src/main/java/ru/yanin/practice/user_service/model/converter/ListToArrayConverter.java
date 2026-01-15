package ru.yanin.practice.user_service.model.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@WritingConverter
public class ListToArrayConverter implements Converter<List<String>, String[]> {
    @Override
    public String[] convert(List<String> source) {
        return source.toArray(new String[0]);
    }
}
