package ru.yanin.practise.bookservice.repository.user_book;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yanin.practise.bookservice.model.dto.BookDto;
import ru.yanin.practise.bookservice.model.dto.request.FilterAndSortRequest;
import ru.yanin.practise.bookservice.model.dto.request.filter.FilterRequest;
import ru.yanin.practise.bookservice.model.dto.request.sort.FieldNameToSortBy;
import ru.yanin.practise.bookservice.model.dto.request.sort.SortRequest;
import ru.yanin.practise.bookservice.repository.UserBookRepository;
import ru.yanin.shared.genre.Genre;
import ru.yanin.shared.language.Language;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Slf4j
@SuppressWarnings("SqlSourceToSinkFlow")
public class UserBookRepositoryImpl implements UserBookRepository {

    private final SimpleJdbcInsert userBookJdbcInsert;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final RowMapper<BookDto> rowMapper;

    public UserBookRepositoryImpl(SimpleJdbcInsert userBookJdbcInsert,
                                  NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.userBookJdbcInsert = userBookJdbcInsert;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.rowMapper = new BookDtoRowMapper();
    }

    @Override
    public void tieBookToUser(Long userId, Long bookId) {
        try {
            userBookJdbcInsert.execute(Map.of(
                    "user_id", userId,
                    "book_id", bookId
            ));
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
    public int deleteBooksFromUser(Long userId, long... bookIds) throws DataAccessException {
        var params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("bookIds", bookIds);
        String query = SqlQueries.deleteBookFromUserQuery();
        log.info(query);
        return namedParameterJdbcTemplate.update(query, params);
    }

    @Override
    public List<BookDto> findAllByUserId(Long userId, int page, int size) {
        var params = userIdPaginationSqlParamSource(userId, page, size);
        String query = SortQueryBuilder.buildSortQuery(FieldNameToSortBy.DEFAULT, true, null);
        log.info(query);
        return namedParameterJdbcTemplate.query(query, params, rowMapper);
    }

    private MapSqlParameterSource userIdPaginationSqlParamSource(Long userId, int page, int size) {
        int offset = page * size;
        return new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("offset", offset)
                .addValue("limit", size);
    }

    @Override
    public List<BookDto> findAllByUserId(Long userId, Language lang,
                                         SortRequest sortRequest) {
        String query = SortQueryBuilder.buildSortQuery(sortRequest.fieldName(), sortRequest.isAscending(), lang);
        var params = userIdPaginationSqlParamSource(userId, sortRequest.page(), sortRequest.size());
        log.info(query);
        return namedParameterJdbcTemplate.query(query, params, rowMapper);
    }

    @Override
    public List<BookDto> findAllByUserIdAndFavoriteGenres(Long userId, Set<Genre> genres, int page, int size) {
        String query = SortQueryBuilder.buildByGenreSortQuery(FieldNameToSortBy.DEFAULT, true, null);
        var params = userIdAndGenresPaginationSqlParamSource(userId, genres, page, size);
        log.info(query);
        return namedParameterJdbcTemplate.query(query, params, rowMapper);
    }

    private MapSqlParameterSource userIdAndGenresPaginationSqlParamSource(Long userId, Set<Genre> genres,
                                                                                   int page, int size) {
        return userIdPaginationSqlParamSource(userId, page, size)
                .addValue("genres", genres.stream().map(Enum::name).collect(Collectors.toSet()));
    }

    @Override
    public List<BookDto> findAllByUserIdAndFavoriteGenres(Long userId, Set<Genre> genres, Language lang,
                                                          SortRequest sortRequest) {

        String query = SortQueryBuilder.buildByGenreSortQuery(sortRequest.fieldName(), sortRequest.isAscending(), lang);
        var params = userIdAndGenresPaginationSqlParamSource(userId, genres, sortRequest.page(), sortRequest.size());
        log.info(query);
        return namedParameterJdbcTemplate.query(query, params, rowMapper);
    }

    @Override
    public List<BookDto> findAllWithFiltering(Long userId, FilterRequest filterRequest) {
        String query = FilterQueryBuilder.buildQueryWithConditions(filterRequest);
        var params = userIdPaginationSqlParamSource(userId, filterRequest.page(), filterRequest.size());
        log.info(query);
        return namedParameterJdbcTemplate.query(query, params, rowMapper);
    }

    @Override
    public List<BookDto> findAllWithFiltering(Long userId, FilterAndSortRequest request, Language lang) {
        var filterRequest = request.filterRequest();
        String query = FilterQueryBuilder.buildQueryWithConditions(filterRequest);
        query = SortQueryBuilder.buildSortQuery(query, request.fieldName(), request.isAscending(), lang);
        var params = userIdPaginationSqlParamSource(userId, filterRequest.page(), filterRequest.size());
        log.info(query);
        return namedParameterJdbcTemplate.query(query, params, rowMapper);
    }
}
