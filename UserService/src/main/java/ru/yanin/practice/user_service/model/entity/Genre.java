package ru.yanin.practice.user_service.model.entity;

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
    LETTERS;

    public static boolean isValidValue(String value) {
        for (var genre : values()) {
            if (genre.name().equals(value)) {
                return true;
            }
        }
        return false;
    }
}