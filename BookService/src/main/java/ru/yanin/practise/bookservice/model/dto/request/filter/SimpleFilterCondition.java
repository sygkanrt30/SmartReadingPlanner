package ru.yanin.practise.bookservice.model.dto.request.filter;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record SimpleFilterCondition(
        @NotNull FieldNameToFilterBy fieldName,
        @NotEmpty Set<String> value
) {
}
