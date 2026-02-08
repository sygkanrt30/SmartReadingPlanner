package ru.yanin.practise.bookservice.service.book.import_book;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yanin.practise.bookservice.model.dto.BookDto;
import ru.yanin.practise.bookservice.model.dto.GoogleBooksResponse;
import ru.yanin.practise.bookservice.model.entity.Book;
import ru.yanin.practise.bookservice.model.mapper.BookMapper;
import ru.yanin.practise.bookservice.service.book.BookService;
import ru.yanin.practise.bookservice.service.book.cache.CacheService;
import ru.yanin.practise.bookservice.service.book.google_book.GoogleBookService;

import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookImportServiceImpl implements BookImportService {

    private final BookMapper bookMapper;
    private final BookService bookService;
    private final GoogleBookService googleBookService;
    private final CacheService<String, BookDto> cacheService;

    @Override
    public BookDto importBookByName(String title, Long userId) {
        Optional<Book> bookFromStores = findBookInStores(title, bookService::findByTitle);
        return importBook(bookFromStores, title, userId, googleBookService::searchByName);
    }

    @Override
    public BookDto importBookByIsbn(String isbn, Long userId) {
        Optional<Book> bookFromStores = findBookInStores(isbn, bookService::findByIsbn);
        return importBook(bookFromStores, isbn, userId, googleBookService::searchByISBN);
    }

    private Optional<Book> findBookInStores(String identParam,
                                            Function<String, Optional<Book>> function) {
        Optional<Book> book = function.apply(identParam);
        if (book.isPresent()) {
            log.trace("Book found in db: {}", identParam);
            return book;
        }
        Optional<BookDto> bookDto = cacheService.get(identParam);
        if (bookDto.isPresent()) {
            log.warn("Book found in cache: {}. But book not found in db", identParam);
            return Optional.of(bookService.save(bookDto.get()));
        }
        return Optional.empty();
    }

    private BookDto importBook(Optional<Book> optionalBook, String identParam, Long userId,
                               Function<String, GoogleBooksResponse> function) {
        return switch (optionalBook.isEmpty()) {
            case true -> importIfBookNotInStores(identParam, userId, function);
            case false -> importIfBookInStore(identParam, optionalBook.get(), userId);
        };
    }

    private BookDto importIfBookNotInStores(String identParam, Long userId,
                                            Function<String, GoogleBooksResponse> function) {

        log.debug("Book with {} not found in storages", identParam);
        GoogleBooksResponse.BookItem bookItem = function.apply(identParam)
                .items()
                .getFirst();
        BookDto bookDto = bookService.save(bookItem, userId);
        cacheService.cache(identParam, bookDto);
        log.debug("Book saved with id {} in db and in stores", bookDto.bookId());
        return bookDto;
    }

    private BookDto importIfBookInStore(String identParam, Book book, Long userId) {
        log.debug("Book with id {} found in storages", book.getId());
        bookService.tieBookToUser(userId, book.getId());
        var bookDto = bookMapper.toBookDto(book);
        if (cacheService.get(identParam).isEmpty()) {
            cacheService.cache(identParam, bookDto);
            log.debug("Book with {} add to cache", identParam);
        }
        return bookDto;
    }

    @Override
    public BookDto previewBook(String isbn) {
        Optional<Book> bookFromStores = findBookInStores(isbn, bookService::findByIsbn);
        if (bookFromStores.isPresent()) {
            log.trace("Book found in stores: {}", isbn);
            return bookMapper.toBookDto(bookFromStores.get());
        }

        GoogleBooksResponse.BookItem preview = googleBookService.searchByISBN(isbn)
                .items()
                .getFirst();
        log.debug("Book got from google api: {}", preview);
        return bookMapper.toBookDto(preview);
    }
}