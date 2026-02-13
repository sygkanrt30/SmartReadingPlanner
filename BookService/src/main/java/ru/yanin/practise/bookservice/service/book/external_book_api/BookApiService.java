package ru.yanin.practise.bookservice.service.book.external_book_api;

import ru.yanin.practise.bookservice.model.dto.BookDto;

import java.util.Optional;

public interface BookApiService {

    Optional<BookDto> searchByISBN(String isbn);

    Optional<BookDto> searchByTitle(String title);
}
