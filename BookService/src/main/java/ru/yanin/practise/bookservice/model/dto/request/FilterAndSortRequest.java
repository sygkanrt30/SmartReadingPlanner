package ru.yanin.practise.bookservice.model.dto.request;

import jakarta.validation.constraints.NotNull;
import ru.yanin.practise.bookservice.model.dto.request.filter.FilterRequest;
import ru.yanin.practise.bookservice.model.dto.request.sort.FieldNameToSortBy;

import java.util.Objects;

public record FilterAndSortRequest(
        @NotNull FilterRequest filterRequest,
        @NotNull FieldNameToSortBy fieldName,
        Boolean isAscending
) {

    public FilterAndSortRequest {
        if (Objects.isNull(isAscending)) isAscending = true;
    }
}
