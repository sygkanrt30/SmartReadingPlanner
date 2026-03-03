package ru.yanin.practise.bookservice.model.dto.request.filter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FilterOperator {
    /**
     * Equal to operator (==)
     */
    EQ("="),

    /**
     * Not equal to operator (!=)
     */
    NEQ("<>"),

    /**
     * Greater than operator (>)
     */
    GT(">"),

    /**
     * Less than operator (<)
     */
    LT("<"),

    /**
     * Greater than or equal to operator (>=)
     */
    GTE(">="),

    /**
     * Less than or equal to operator (<=)
     */
    LTE("<=");

    private final String sqlOperator;
}
