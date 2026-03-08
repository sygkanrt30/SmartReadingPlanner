package ru.yanin.practise.bookservice.model.dto;

import ru.yanin.shared.genre.Genre;

public record ReadingPlanCalculationEvent(
        Long bookId,
        Long userId,
        Integer pages,
        Genre genre
) {
}
