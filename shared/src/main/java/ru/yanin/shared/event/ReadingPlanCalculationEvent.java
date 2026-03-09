package ru.yanin.shared.event;

import ru.yanin.shared.genre.Genre;

public record ReadingPlanCalculationEvent(
        Long bookId,
        Long userId,
        Integer pages,
        Genre genre
) {
}
