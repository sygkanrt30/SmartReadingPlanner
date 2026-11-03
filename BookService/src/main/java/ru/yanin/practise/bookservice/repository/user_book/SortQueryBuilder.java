package ru.yanin.practise.bookservice.repository.user_book;

import lombok.experimental.UtilityClass;
import ru.yanin.practise.bookservice.model.dto.request.sort.FieldNameToSortBy;
import ru.yanin.shared.language.Language;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

import static org.springframework.data.domain.Sort.Direction;
import static ru.yanin.shared.language.Language.ENGLISH;
import static ru.yanin.shared.language.Language.RUSSIAN;

@UtilityClass
class SortQueryBuilder {

    private static final Map<Language, String> LANG_MAP = new EnumMap<>(Language.class);

    static {
        LANG_MAP.put(ENGLISH, "b.title COLLATE \"en_US\"");
        LANG_MAP.put(RUSSIAN, "b.title COLLATE \"ru_RU\"");
    }

    String buildSortQuery(FieldNameToSortBy field, boolean ascending, Language lang) {
        return buildQuery(ascending, field, lang, SqlQueries.selectUserBooksSortQuerySample());
    }

    String buildSortQuery(String query, FieldNameToSortBy field, boolean ascending, Language lang){
        return buildQuery(ascending, field, lang, query);
    }

    String buildByGenreSortQuery(FieldNameToSortBy field, boolean ascending, Language lang) {
        return buildQuery(ascending, field, lang, SqlQueries.selectByUserIdAndGenresQuerySample());
    }

    private String buildQuery(boolean ascending, FieldNameToSortBy field, Language lang, String sample) {
        String orderDirection = (ascending ? Direction.ASC : Direction.DESC).name();
        String orderByClause = getOrderByClause(field, lang);
        return String.format(sample, orderByClause, orderDirection);
    }

    private String getOrderByClause(FieldNameToSortBy field, Language lang) {
        return switch (field) {
            case PAGES, PUBLISHED_DATE, AVERAGE_RATING, DEFAULT -> field.getSqlExpression();
            case TITLE -> getTitleOrderByClause(lang);
        };
    }

    private String getTitleOrderByClause(Language lang) {
        if (Objects.isNull(lang)) {
            return FieldNameToSortBy.TITLE.name();
        }
        return LANG_MAP.get(lang);
    }
}
