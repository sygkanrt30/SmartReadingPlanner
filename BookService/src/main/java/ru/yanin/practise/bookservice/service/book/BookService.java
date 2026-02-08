package ru.yanin.practise.bookservice.service.book;

import ru.yanin.practise.bookservice.model.dto.BookDto;
import ru.yanin.practise.bookservice.model.dto.GoogleBooksResponse;
import ru.yanin.practise.bookservice.model.entity.Book;

import java.util.Optional;

public interface BookService {

    BookDto save(GoogleBooksResponse.BookItem bookItem, Long userId);

    void tieBookToUser(Long userId, Long bookId);

    Book save(BookDto bookDto);

    Optional<Book> findByIsbn(String isbn);

    Optional<Book> findByTitle(String title);
}
