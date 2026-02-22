package ru.yanin.practise.bookservice.service.book.user_book;

import jakarta.validation.constraints.NotNull;
import ru.yanin.practise.bookservice.model.dto.BookDto;
import ru.yanin.practise.bookservice.model.dto.sort_request.SortAndPaginationRequest;

import java.util.List;

public interface UserBookService {

    void tieBookToUser(Long userId, Long bookId, String isbn);

    void removeBooksFromUser(Long userId, @NotNull Long... bookIds);

    List<BookDto> findAllWithPagination(Long userId, int page, int size);

    List<BookDto> findAllWithPaginationAndSort(Long userId, SortAndPaginationRequest sortRequest);
}
