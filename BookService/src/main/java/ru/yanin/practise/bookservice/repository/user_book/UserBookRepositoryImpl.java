package ru.yanin.practise.bookservice.repository.user_book;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yanin.practise.bookservice.model.dto.BookDto;
import ru.yanin.practise.bookservice.model.dto.sort_request.SortAndPaginationRequest;
import ru.yanin.practise.bookservice.repository.UserBookRepository;
import ru.yanin.shared.language.Language;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
@Slf4j
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
        String query = SqlQueries.countByUserAndBookIdsQuery();
        log.info(query);
        return namedParameterJdbcTemplate.queryForObject(query, params, Long.class);
    }

    @Override
    public int deleteBooksFromUser(Long userId, Long... bookIds) throws DataAccessException {
        var params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("bookIds", bookIds);
        String query = SqlQueries.deleteBookFromUserQuery();
        log.info(query);
        return namedParameterJdbcTemplate.update(query, params);
    }

    @Override
    public List<String> findAllISBNByUserIdWithPagination(Long userId, int page, int size) {
        var params = getUserIdPaginationSqlParamSource(userId, page, size);
        String query = SqlQueries.selectUserBooksWithPaginationQuery();
        log.info(query);
        return namedParameterJdbcTemplate.queryForList(query, params, String.class);
    }

    private @NonNull MapSqlParameterSource getUserIdPaginationSqlParamSource(Long userId, int page, int size) {
        int offset = page * size;
        return new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("offset", offset)
                .addValue("limit", size);
    }

    @Override
    public List<BookDto> findAllByUserIdWithPaginationAndSort(Long userId, Language lang,
                                                              SortAndPaginationRequest sortRequest) {
        String sql = SortQueryBuilder.buildSortQuery(sortRequest.fieldName(), sortRequest.isAscending(), lang);
        var params = getUserIdPaginationSqlParamSource(userId, sortRequest.page(), sortRequest.size());
        log.info(sql);
        return namedParameterJdbcTemplate.queryForList(sql, params, BookDto.class);
    }
}
