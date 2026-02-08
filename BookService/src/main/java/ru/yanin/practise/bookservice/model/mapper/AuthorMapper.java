package ru.yanin.practise.bookservice.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;
import ru.yanin.practise.bookservice.model.entity.Author;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthorMapper {

    @Mapping(target = "fullName", source = "authorName")
    Author toAuthor(String authorName);
}
