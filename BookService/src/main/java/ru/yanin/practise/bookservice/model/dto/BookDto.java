package ru.yanin.practise.bookservice.model.dto;

import ru.yanin.shared.genre.Genre;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public record BookDto(
        String isbn,
        String title,
        Integer pages,
        String publisher,
        LocalDate publishedDate,
        String language,
        Genre genre,
        String description,
        BigDecimal averageRating,
        String imageLink,
        Set<AuthorDto> authors
) {
}
