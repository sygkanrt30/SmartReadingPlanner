package ru.yanin.practise.bookservice.repository.user_book;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yanin.practise.bookservice.model.dto.sort_request.SortAndPaginationRequest;
import ru.yanin.practise.bookservice.repository.UserBookRepository;
import ru.yanin.shared.language.Language;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class UserBookRepositoryImpl implements UserBookRepository {

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
        return namedParameterJdbcTemplate.queryForObject(SqlQueries.countByUserAndBookIdsQuery(), params, Long.class);
    }

    @Override
    public int deleteBooksFromUser(Long userId, Long... bookIds) throws DataAccessException {
        var params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("bookIds", bookIds);
        return namedParameterJdbcTemplate.update(SqlQueries.deleteBookFromUserQuery(), params);
    }

    @Override
    public List<String> findAllISBNByUserIdWithPagination(Long userId, int page, int size) {
        var params = getUserIdPaginationSqlParamSource(userId, page, size);
        return namedParameterJdbcTemplate.queryForList(
                SqlQueries.selectUserBooksWithPaginationQuery(), params, String.class);
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
        String sql = SortQueryBuilder.buildSortQuery(sortRequest.fieldName(), sortRequest.isAscending(), lang);
        var params = getUserIdPaginationSqlParamSource(userId, sortRequest.page(), sortRequest.size());
        return namedParameterJdbcTemplate.queryForList(sql, params, String.class);
    }
}
