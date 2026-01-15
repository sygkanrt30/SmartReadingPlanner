package ru.yanin.practice.user_service.model.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;
import ru.yanin.practice.user_service.exception.DataConvertFailException;

import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@ReadingConverter
@Slf4j
public class ArrayToListConverter implements Converter<Array, List<String>> {

    @Override
    public List<String> convert(Array source) {
        try {
            var array = (String[]) source.getArray();
            return array != null ? new ArrayList<>(Arrays.asList(array)) : new ArrayList<>();
        } catch (SQLException e) {
            throw new DataConvertFailException("Failed to convert array", e);
        }
    }
}
