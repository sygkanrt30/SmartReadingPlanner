package ru.yanin.practise.bookservice.model.dto.request.sort;

import jakarta.validation.constraints.NotEmpty;

import java.util.Objects;

public record SortRequest(
        @NotEmpty FieldNameToSortBy fieldName,
        Boolean isAscending,
        Integer page,
        Integer size
) {
    public SortRequest {
        if (Objects.isNull(isAscending)) isAscending = true;
        if (Objects.isNull(page)) page = 0;
        if (Objects.isNull(size)) size = 15;
    }
}
