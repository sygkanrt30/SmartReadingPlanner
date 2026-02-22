package ru.yanin.practise.bookservice.model.dto.sort_request;

import jakarta.validation.constraints.NotEmpty;

import java.util.Objects;

public record SortAndPaginationRequest(
        @NotEmpty FieldNameToSortBy fieldName,
        Boolean isAscending,
        Integer page,
        Integer size
) {
    public SortAndPaginationRequest {
        if (Objects.isNull(isAscending)) isAscending = true;
        if (Objects.isNull(page)) page = 0;
        if (Objects.isNull(size)) size = 15;
    }
}
