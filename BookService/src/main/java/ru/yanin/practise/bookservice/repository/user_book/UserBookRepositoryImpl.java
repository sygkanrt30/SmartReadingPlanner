package ru.yanin.practise.bookservice.repository.user_book;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
@RequiredArgsConstructor
public class UserBookRepositoryImpl implements UserBookRepository {

    private static final String COUNT_BY_USER_AND_BOOK_IDS_QUERY =
            "SELECT COUNT(*) FROM users_books WHERE user_id = :userId AND book_id = :bookId";
    private static final String DELETE_BOOK_FROM_USER_QUERY =
            "DELETE FROM users_books WHERE user_id = :userId and book_id = :bookId";

    private final SimpleJdbcInsert userBookJdbcInsert;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void tieBookToUser(Long userId, Long bookId) {
        try {
            userBookJdbcInsert.execute(Map.of(
                    "user_id", userId,
                    "book_id", bookId));
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    @Override
    public Long countRowByUserAndBookId(Long userId, Long bookId) {
        var params = getSqlParameterSource(userId, bookId);
        return namedParameterJdbcTemplate.queryForObject(COUNT_BY_USER_AND_BOOK_IDS_QUERY, params, Long.class);
    }

    private @NonNull MapSqlParameterSource getSqlParameterSource(Long userId, Long bookId) {
        return new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("bookId", bookId);
    }

    @Override
    public int deleteBookFromUser(Long userId, Long bookId) throws DataAccessException {
        var params = getSqlParameterSource(userId, bookId);
        return namedParameterJdbcTemplate.update(DELETE_BOOK_FROM_USER_QUERY, params);
    }
}
