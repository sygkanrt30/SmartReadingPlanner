package ru.yanin.practise.bookservice.service.book.import_book;

import ru.yanin.practise.bookservice.model.dto.BookDto;

public interface BookImportService {

    BookDto importBookByIsbn(String isbn, Long userId);

    BookDto importBookByTitle(String bookName, Long userId);

    BookDto importBookByIsbnAndSendEventForPlanning(String isbn, Long userId);

    BookDto importBookByTitleAndSendEventForPlanning(String bookName, Long userId);

    BookDto previewBook(String isbn);
}
