package ru.yanin.practise.bookservice.service.book;

import ru.yanin.practise.bookservice.model.dto.BookDto;
import ru.yanin.practise.bookservice.model.entity.Book;

import java.util.Optional;

public interface BookService {

    Book save(BookDto bookDto);

    BookDto save(BookDto bookDto, Long userId);

    Optional<Book> findByIsbn(String isbn);

    Optional<Book> findByTitle(String title);
}
