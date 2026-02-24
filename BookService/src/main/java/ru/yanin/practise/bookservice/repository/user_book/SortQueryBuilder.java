package ru.yanin.practise.bookservice.repository.user_book;

import lombok.experimental.UtilityClass;
import ru.yanin.practise.bookservice.model.dto.sort_request.FieldNameToSortBy;
import ru.yanin.shared.language.Language;

import java.util.Objects;

import static org.springframework.data.domain.Sort.Direction;

@UtilityClass
class SortQueryBuilder {

    String buildSortQuery(FieldNameToSortBy field, boolean ascending, Language lang) {
        String orderDirection = (ascending ? Direction.ASC : Direction.DESC).name();
        String orderByClause = getOrderByClause(field, lang);
        return String.format(SqlQueries.selectUserBooksSortQuerySample(), orderByClause, orderDirection);
    }

    private String getOrderByClause(FieldNameToSortBy field, Language lang) {
        return switch (field) {
            case TITLE -> getTitleOrderByClause(lang);
            case PAGES -> "b.pages";
            case PUBLISHED_DATE -> "b.published_date";
            case AVERAGE_RATING -> "b.average_rating";
        };
    }

    private String getTitleOrderByClause(Language lang) {
        if (Objects.isNull(lang)) {
            return "b.title";
        }
        return switch (lang) {
            case RUSSIAN -> "b.title COLLATE \"ru_RU\"";
            case ENGLISH -> "b.title COLLATE \"en_US\"";
        };
    }
}
