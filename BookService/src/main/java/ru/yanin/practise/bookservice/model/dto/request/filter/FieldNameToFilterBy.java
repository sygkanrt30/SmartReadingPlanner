package ru.yanin.practise.bookservice.model.dto.request.filter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FieldNameToFilterBy {
    GENRE("b.genre"),
    PAGES("b.pages"),
    PUBLISHED_DATE("b.published_date"),
    AVERAGE_RATING("b.average_rating"),
    LANG("b.language");

    private final String sqlExpression;
}
