package ru.yanin.practise.bookservice.model.dto.sort_request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;

public record SortAndPaginationRequest(
        @NotEmpty FieldNameToSortBy fieldName,
        @JsonProperty(defaultValue = "true") boolean isAscending,
        @JsonProperty(defaultValue = "0") int page,
        @JsonProperty(defaultValue = "15") int size
) {
}
