package ru.yanin.practise.bookservice.model.dto.request.filter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ComplicateFilterCondition(
        @NotNull FieldNameToFilterBy fieldName,
        @NotBlank Object value,
        @NotNull FilterOperator operator
) {
}
