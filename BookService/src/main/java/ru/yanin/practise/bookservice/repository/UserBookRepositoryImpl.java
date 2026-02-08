package ru.yanin.practise.bookservice.repository;

import lombok.RequiredArgsConstructor;
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
        var params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("bookId", bookId);
        return namedParameterJdbcTemplate.queryForObject(COUNT_BY_USER_AND_BOOK_IDS_QUERY, params, Long.class);
    }
}
