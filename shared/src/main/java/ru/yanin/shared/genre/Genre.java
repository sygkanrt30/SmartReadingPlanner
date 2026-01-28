package ru.yanin.shared.genre;

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


    public static Genre genreFrom(String value){
        for (var genre : values()) {
            if (genre.name().equalsIgnoreCase(value)) {
                return genre;
            }
        }
        return NO_GENRE;
    }
}
