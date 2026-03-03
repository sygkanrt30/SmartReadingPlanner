package ru.yanin.shared.genre;

import java.util.Objects;

public enum Genre {
    FICTION,
    NON_FICTION,
    FANTASY,
    SCIENCE_FICTION,
    MYSTERY,
    ROMANCE,
    HORROR,
    BIOGRAPHY,
    HISTORY,
    POETRY,
    DRAMA,
    COMEDY,
    THRILLER,
    ADVENTURE,
    CHILDREN,
    YOUNG_ADULT,
    CLASSIC,
    GRAPHIC_NOVEL,
    SELF_HELP,
    SCIENCE,
    COMPUTERS,
    SATIRE,
    DYSTOPIAN,
    UTOPIA,
    WESTERN,
    CRIME,
    NOIR,
    PSYCHOLOGICAL,
    PHILOSOPHICAL,
    EPIC,
    SHORT_STORY,
    NOVELLA,
    ESSAY,
    DIARY,
    LETTERS,

    NO_GENRE;


    public static Genre genreFrom(String value) {
        if (Objects.nonNull(value)) {
            for (var genre : values()) {
                if (genre.name().equalsIgnoreCase(value)) {
                    return genre;
                }
            }
        }
        return NO_GENRE;
    }
}
