package ru.yanin.practise.bookservice.repository.user_book;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import ru.yanin.practise.bookservice.model.dto.AuthorDto;
import ru.yanin.practise.bookservice.model.dto.BookDto;
import ru.yanin.shared.genre.Genre;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

@Slf4j
final class BookDtoRowMapper implements RowMapper<BookDto> {

    @Override
    public BookDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        String authorsJson = rs.getString("authors");
        Set<AuthorDto> authors = parseAuthors(authorsJson);
        return BookDto.builder()
                .bookId(rs.getLong("id"))
                .isbn(rs.getString("isbn"))
                .title(rs.getString("title"))
                .pages(rs.getInt("pages"))
                .publisher(rs.getString("publisher"))
                .publishedDate(getPublishedDate(rs))
                .language(rs.getString("language"))
                .genre(Genre.valueOf(rs.getString("genre")))
                .description(rs.getString("description"))
                .averageRating(rs.getBigDecimal("average_rating"))
                .imageLink(rs.getString("image_link"))
                .authors(authors)
                .build();
    }

    private LocalDate getPublishedDate(ResultSet rs) throws SQLException {
        Date sqlDate = rs.getDate("published_date");
        if (Objects.isNull(sqlDate)) return null;
        return sqlDate.toLocalDate();
    }

    private Set<AuthorDto> parseAuthors(String authorsJson) {
        if (Objects.isNull(authorsJson) || authorsJson.isEmpty()) {
            return Collections.emptySet();
        }
        try {
            var mapper = new ObjectMapper();
            JavaType type = mapper.getTypeFactory().constructCollectionType(Set.class, AuthorDto.class);
            return mapper.readValue(authorsJson, type);
        } catch (JsonProcessingException e) {
            log.error("Error parsing authors JSON: {}", authorsJson, e);
            return Collections.emptySet();
        }
    }
}
