package ru.yanin.practise.bookservice.service.book;

import ru.yanin.practise.bookservice.model.dto.BookDto;

public interface BookImportService {

    BookDto importBookByIsbn(String isbn, Long userId);

    BookDto importBookByName(String bookName, Long userId);

    BookDto previewBook(String isbn);
}
