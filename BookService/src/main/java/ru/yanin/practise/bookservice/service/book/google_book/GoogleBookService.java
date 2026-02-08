package ru.yanin.practise.bookservice.service.book.google_book;

import ru.yanin.practise.bookservice.model.dto.GoogleBooksResponse;

public interface GoogleBookService {

    GoogleBooksResponse searchByISBN(String isbn);

    GoogleBooksResponse searchByName(String bookName);
}
