package ru.yanin.practise.bookservice.model.dto.request.sort;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FieldNameToSortBy {
    TITLE("b.title"),
    PAGES("b.pages"),
    PUBLISHED_DATE("b.published_date"),
    AVERAGE_RATING("b.average_rating"),
    DEFAULT("ub.book_id");

    private final String sqlExpression;

    @JsonCreator
    public static FieldNameToSortBy fromString(String s) {
        for (var value : FieldNameToSortBy.values()) {
            if (value.name().equalsIgnoreCase(s)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Passed string doesn't match any constants");
    }
}
