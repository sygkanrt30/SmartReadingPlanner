package ru.yanin.practise.bookservice.repository;

import org.springframework.dao.DataAccessException;
import ru.yanin.practise.bookservice.model.dto.BookDto;
import ru.yanin.practise.bookservice.model.dto.request.FilterAndSortRequest;
import ru.yanin.practise.bookservice.model.dto.request.filter.FilterRequest;
import ru.yanin.practise.bookservice.model.dto.request.sort.SortRequest;
import ru.yanin.shared.genre.Genre;
import ru.yanin.shared.language.Language;

import java.util.List;
import java.util.Set;

public interface UserBookRepository {

    void tieBookToUser(Long userId, Long bookId);

    Long countRowByUserAndBookId(Long userId, Long bookId);

    /**
     * Removes the string consisting of bookId and userId.
     * @param userId the current authenticated user id
     * @param bookId the book id
     * @return the number of rows affected
     * @throws DataAccessException if there is any problem issuing the remove
     */
    int deleteBooksFromUser(Long userId, long... bookId) throws DataAccessException;

    List<BookDto> findAllByUserId(Long userId, int page, int size);

    List<BookDto> findAllByUserId(Long userId, Language lang, SortRequest sortRequest);

    List<BookDto> findAllByUserIdAndFavoriteGenres(Long userId, Set<Genre> genres, int page, int size);

    List<BookDto> findAllByUserIdAndFavoriteGenres(Long userId, Set<Genre> genres, Language lang,
                                                   SortRequest sortRequest);

    List<BookDto> findAllWithFiltering(Long userId, FilterRequest filterRequest);

    List<BookDto> findAllWithFiltering(Long userId, FilterAndSortRequest request, Language lang);
}
