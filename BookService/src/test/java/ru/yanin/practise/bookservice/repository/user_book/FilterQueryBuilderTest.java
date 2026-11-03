package ru.yanin.practise.bookservice.repository.user_book;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yanin.practise.bookservice.model.dto.request.filter.ComplicateFilterCondition;
import ru.yanin.practise.bookservice.model.dto.request.filter.FilterRequest;
import ru.yanin.practise.bookservice.model.dto.request.filter.SimpleFilterCondition;
import ru.yanin.practise.bookservice.repository.user_book.util.QueryReader;
import ru.yanin.shared.genre.Genre;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.yanin.practise.bookservice.model.dto.request.filter.FieldNameToFilterBy.*;
import static ru.yanin.practise.bookservice.model.dto.request.filter.FilterOperator.*;


class FilterQueryBuilderTest {

    private final QueryReader reader = new QueryReader(
            "/ru/yanin/practise/bookservice/repository/user_book/user_book_queries_test.yaml");


    @Test
    @DisplayName("Should Return Sql query if all conditions present")
    void buildQueryWithConditions1() {
        var pagesCondition = new ComplicateFilterCondition(PAGES, 390, LT);
        var dateCondition = new ComplicateFilterCondition(PUBLISHED_DATE,
                LocalDate.of(2024, 12, 12), EQ);
        var ratingCondition = new ComplicateFilterCondition(AVERAGE_RATING, 3.5, LTE);
        var genreCondition = new SimpleFilterCondition(GENRE, new TreeSet<>(Set.of(Genre.ADVENTURE.name(), Genre.BIOGRAPHY.name())));
        var langCondition = new SimpleFilterCondition(LANG, Set.of("en"));
        var filterRequest = new FilterRequest(List.of(pagesCondition, dateCondition, ratingCondition),
                List.of(genreCondition, langCondition), 0, 15);
        String expectedQuery = reader.get("TEST1");

        String result = FilterQueryBuilder.buildQueryWithConditions(filterRequest);

        assertEquals(expectedQuery, result);
    }

    @Test
    @DisplayName("Should Return Sql query if 4 of 5 condition present and 1 invalid condition")
    void buildQueryWithConditions2() {
        var pagesCondition = new ComplicateFilterCondition(PAGES, 500, NEQ);
        var dateCondition = new ComplicateFilterCondition(PUBLISHED_DATE,
                LocalDate.of(2011, 5, 12), LT);
        var ratingCondition = new ComplicateFilterCondition(GENRE, 3.5, LTE);
        var langCondition = new SimpleFilterCondition(LANG, Set.of("en"));
        var filterRequest = new FilterRequest(List.of(pagesCondition, dateCondition, ratingCondition),
                List.of(langCondition), 0, 15);
        String expectedQuery = reader.get("TEST2");

        String result = FilterQueryBuilder.buildQueryWithConditions(filterRequest);

        assertEquals(expectedQuery, result);
    }

    @Test
    @DisplayName("Should Return Sql query if 2 of 5 condition present and 2 invalid condition")
    void buildQueryWithConditions3() {
        var pagesCondition = new ComplicateFilterCondition(LANG, 500, NEQ);
        var ratingCondition = new ComplicateFilterCondition(GENRE, 3.5, LTE);
        var filterRequest = new FilterRequest(List.of(pagesCondition, ratingCondition),
                null, 0, 15);
        String expectedQuery = reader.get("TEST3");

        String result = FilterQueryBuilder.buildQueryWithConditions(filterRequest);

        assertEquals(expectedQuery, result);
    }

    @Test
    @DisplayName("Should Return Sql query if 2 of 5 same condition present")
    void buildQueryWithConditions4() {
        var pagesCondition = new ComplicateFilterCondition(PAGES, 390, LT);
        var secondPagesCondition = new ComplicateFilterCondition(PAGES, 120, GTE);
        var filterRequest = new FilterRequest(List.of(pagesCondition, secondPagesCondition), null, 0, 15);
        String expectedQuery = reader.get("TEST4");

        String result = FilterQueryBuilder.buildQueryWithConditions(filterRequest);

        assertEquals(expectedQuery, result);
    }

    @Test
    @DisplayName("Should Return default Sql query if 0 of 5 condition present")
    void buildQueryWithConditions5() {
        var filterRequest = new FilterRequest(null, null, null, null);
        String expectedQuery = reader.get("TEST5");

        String result = FilterQueryBuilder.buildQueryWithConditions(filterRequest);

        assertEquals(expectedQuery, result);
    }
}