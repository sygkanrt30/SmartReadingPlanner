package ru.yanin.practise.bookservice.model.dto.request.filter;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record FilterRequest(
        @JsonProperty("complicate_conditions")
        List<ComplicateFilterCondition> complicateConditions,
        @JsonProperty("simple_conditions")
        List<SimpleFilterCondition> simpleConditions,
        Integer page,
        Integer size
) {

    public FilterRequest {
        if (Objects.isNull(page)) page = 0;
        if (Objects.isNull(size)) size = 15;
        if (Objects.isNull(complicateConditions))  complicateConditions = new ArrayList<>();
        if (Objects.isNull(simpleConditions)) simpleConditions = new ArrayList<>();
    }
}
