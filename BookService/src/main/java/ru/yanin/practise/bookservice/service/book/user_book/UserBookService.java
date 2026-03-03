package ru.yanin.practise.bookservice.service.book.user_book;

import jakarta.validation.constraints.NotNull;
import ru.yanin.practise.bookservice.model.dto.BookDto;
import ru.yanin.practise.bookservice.model.dto.request.FilterAndSortRequest;
import ru.yanin.practise.bookservice.model.dto.request.filter.FilterRequest;
import ru.yanin.practise.bookservice.model.dto.request.sort.SortRequest;

import java.util.List;

public interface UserBookService {

    void tieBookToUser(Long userId, Long bookId);

    void removeBooksFromUser(Long userId, @NotNull long... bookIds);

    List<BookDto> findAll(Long userId, int page, int size);

    List<BookDto> findAll(Long userId, SortRequest sortRequest);

    List<BookDto> findAllWithFiltering(Long userId, FilterRequest filterRequest);

    List<BookDto> findAllWithFiltering(Long userId, FilterAndSortRequest filterAndSortRequest);

    List<BookDto> findAllByFavoriteGenres(Long userId, int page, int size);

    List<BookDto> findAllByFavoriteGenres(Long userId, SortRequest sortRequest);

}
