package ru.yanin.practise.bookservice.model.dto.sort_request;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum FieldNameToSortBy {
    TITLE,
    PAGES,
    PUBLISHED_DATE,
    AVERAGE_RATING;

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
