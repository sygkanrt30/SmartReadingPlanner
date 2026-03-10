package ru.yanin.practise.bookservice.repository.user_book;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.yanin.practise.bookservice.model.dto.request.filter.ComplicateFilterCondition;
import ru.yanin.practise.bookservice.model.dto.request.filter.FieldNameToFilterBy;
import ru.yanin.practise.bookservice.model.dto.request.filter.FilterRequest;
import ru.yanin.practise.bookservice.model.dto.request.filter.SimpleFilterCondition;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.yanin.practise.bookservice.model.dto.request.filter.FieldNameToFilterBy.*;

@UtilityClass
@Slf4j
class FilterQueryBuilder {

    private static final String AND = " AND ";
    private static final String BASE_CONDITION_STRING = "WHERE ub.user_id = :userId";

    String buildQueryWithConditions(FilterRequest request) {
        var stringBuilder = new StringBuilder(SqlQueries.selectUserBooksSortQuerySample());
        int whereEndIndex = stringBuilder.indexOf(BASE_CONDITION_STRING) + BASE_CONDITION_STRING.length();

        var conditions = new ArrayList<String>();
        request.complicateConditions()
                .forEach(complicateCondition ->
                        addConditionToList(conditions, complicateCondition));
        request.simpleConditions()
                .forEach(simpleCondition ->
                        addConditionToList(conditions, simpleCondition));
        if (!conditions.isEmpty()) {
            String conditionsString = AND + String.join(AND, conditions);
            stringBuilder.insert(whereEndIndex, conditionsString);
        }
        return stringBuilder.toString();
    }

    private void addConditionToList(List<String> conditions,
                                    ComplicateFilterCondition condition) {
        if (Objects.isNull(condition)) return;

        String operator = condition.operator().getSqlOperator();
        Object value = condition.value();
        String fieldExpression = condition.fieldName().getSqlExpression();

        switch (condition.fieldName()) {
            case PAGES -> {
                if (value instanceof Integer pages) {
                    conditions.add(fieldExpression + " " + operator + " " + pages);
                }
            }
            case PUBLISHED_DATE -> {
                if (value instanceof LocalDate date) {
                    conditions.add(fieldExpression + " " + operator + " '" +
                            date.format(DateTimeFormatter.ISO_DATE) + "'");
                }
            }
            case AVERAGE_RATING -> {
                if (value instanceof Number rating) {
                    conditions.add(fieldExpression + " " + operator + " " + rating);
                }
            }
            default -> log.warn("Unknown complicate condition: {}", condition.fieldName());
        }
    }

    private void addConditionToList(List<String> conditions,
                                    SimpleFilterCondition condition) {
        if (Objects.isNull(condition)) return;

        Set<String> values = condition.value();
        if (Objects.isNull(values) || values.isEmpty()) {
            log.warn("Invalid value for condition: {}", condition.fieldName());
            return;
        }
        FieldNameToFilterBy fieldName = condition.fieldName();
        if (!(fieldName.equals(GENRE) || fieldName.equals(LANG))) {
            log.warn("Unknown complicate condition: {}", fieldName);
            return;
        }

        String joinedValues = values.stream()
                .map(v -> "'" + v + "'")
                .collect(Collectors.joining(", "));
        conditions.add(fieldName.getSqlExpression() + " IN (" + joinedValues + ")");
    }
}
