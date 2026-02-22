package ru.yanin.practise.bookservice.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.yanin.practise.bookservice.model.dto.BookDto;
import ru.yanin.practise.bookservice.model.dto.ReadingPlanCalculationEvent;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReadingPlanCalculationEventMapper {

    @Mapping(target = "userId", source = "userId")
    ReadingPlanCalculationEvent toEvent(BookDto bookDto, Long userId);
}
