package ru.yanin.practise.bookservice.repository.user_book;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yanin.practise.bookservice.model.dto.sort_request.FieldNameToSortBy;
import ru.yanin.practise.bookservice.model.dto.sort_request.SortAndPaginationRequest;
import ru.yanin.shared.language.Language;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class UserBookRepositoryImpl implements UserBookRepository {

    private static final String COUNT_BY_USER_AND_BOOK_IDS_QUERY =
            "SELECT COUNT(*) FROM users_books WHERE user_id = :userId AND book_id = :bookId";
    private static final String DELETE_BOOK_FROM_USER_QUERY =
            "DELETE FROM users_books WHERE user_id = :userId AND book_id IN :bookIds";
    private static final String SELECT_BY_USER_ID_WITH_PAGINATION_QUERY =
            "SELECT isbn FROM users_books WHERE user_id = :userId ORDER BY book_id LIMIT :limit OFFSET :offset";
    private static final String SELECT_BY_USER_ID_WITH_SORT_QUERY_SAMPLE =
            """
                SELECT ub.isbn
                FROM users_books ub
                INNER JOIN books b ON ub.book_id = b.id
                WHERE ub.user_id = :userId
                ORDER BY %s %s
                LIMIT :limit OFFSET :offset
                """;

    private final SimpleJdbcInsert userBookJdbcInsert;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void tieBookToUser(Long userId, Long bookId, String isbn) {
        try {
            userBookJdbcInsert.execute(Map.of(
                    "user_id", userId,
                    "book_id", bookId,
                    "isbn", isbn));
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    @Override
    public Long countRowByUserAndBookId(Long userId, Long bookId) {
        var params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("bookId", bookId);
        return namedParameterJdbcTemplate.queryForObject(COUNT_BY_USER_AND_BOOK_IDS_QUERY, params, Long.class);
    }

    @Override
    public int deleteBooksFromUser(Long userId, Long... bookIds) throws DataAccessException {
        var params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("bookIds", bookIds);
        return namedParameterJdbcTemplate.update(DELETE_BOOK_FROM_USER_QUERY, params);
    }

    @Override
    public List<String> findAllISBNByUserIdWithPagination(Long userId, int page, int size) {
        var params = getUserIdPaginationSqlParamSource(userId, page, size);
        return namedParameterJdbcTemplate.queryForList(
                SELECT_BY_USER_ID_WITH_PAGINATION_QUERY, params, String.class);
    }

    private @NonNull MapSqlParameterSource getUserIdPaginationSqlParamSource(Long userId, int page, int size) {
        int offset = page * size;
        return new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("offset", offset)
                .addValue("limit", size);
    }

    @Override
    public List<String> findAllISBNByUserIdWithPaginationAndSort(Long userId, Language lang,
                                                                 SortAndPaginationRequest sortRequest) {
        String sql = buildSortQuery(sortRequest.fieldName(), sortRequest.isAscending(), lang);
        var params = getUserIdPaginationSqlParamSource(userId, sortRequest.page(), sortRequest.size());
        return namedParameterJdbcTemplate.queryForList(sql, params, String.class);
    }

    private String buildSortQuery(FieldNameToSortBy field, boolean ascending, Language lang) {
        String orderDirection = ascending ? "ASC" : "DESC";
        String orderByClause = getOrderByClause(field, lang);
        return String.format(SELECT_BY_USER_ID_WITH_SORT_QUERY_SAMPLE, orderByClause, orderDirection);
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
