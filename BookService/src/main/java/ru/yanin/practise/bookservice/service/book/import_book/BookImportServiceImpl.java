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
        Optional<BookDto> bookFromStores = findBookWithCacheBackupRecovery(title, bookService::findByTitle);
        return importBook(bookFromStores, title, userId, googleBookService::searchByName);
    }

    @Override
    public BookDto importBookByIsbn(String isbn, Long userId) {
        Optional<BookDto> bookFromStores = findBookWithCacheBackupRecovery(isbn, bookService::findByIsbn);
        return importBook(bookFromStores, isbn, userId, googleBookService::searchByISBN);
    }

    private Optional<BookDto> findBookWithCacheBackupRecovery(String identParam,
                                                           Function<String, Optional<Book>> dbLookup) {
        Optional<Book> book = dbLookup.apply(identParam);
        if (book.isPresent()) {
            log.trace("Book found in db: {}", identParam);
            var bookDto = bookMapper.toBookDto(book.get());
            cacheService.cache(identParam, bookDto);
            return Optional.of(bookDto);
        }
        Optional<BookDto> bookDto = cacheService.get(identParam);
        if (bookDto.isPresent()) {
            log.warn("Book found in cache: {}. But book not found in db", identParam);
            bookService.save(bookDto.get());
            return bookDto;
        }
        return Optional.empty();
    }

    private BookDto importBook(Optional<BookDto> optionalBook, String identParam, Long userId,
                               Function<String, GoogleBooksResponse> getBookFromApi) {
        return switch (optionalBook.isPresent()) {
            case true -> {
                var book = optionalBook.get();
                log.debug("Book with id {} found in storages", book.bookId());
                bookService.tieBookToUser(userId, book.bookId());
                yield book;
            }
            case false -> importIfBookNotInStorages(identParam, userId, getBookFromApi);
        };
    }

    private BookDto importIfBookNotInStorages(String identParam, Long userId,
                                              Function<String, GoogleBooksResponse> getBookFromApi) {

        log.debug("Book with {} not found in storages", identParam);
        GoogleBooksResponse.BookItem bookItem = getBookFromApi.apply(identParam)
                .items()
                .getFirst();
        BookDto bookDto = bookService.save(bookItem, userId);
        cacheService.cache(identParam, bookDto);
        log.debug("Book saved with id {} in db and in stores", bookDto.bookId());
        return bookDto;
    }

    @Override
    public BookDto previewBook(String isbn) {
        Optional<BookDto> bookFromStores = findBookWithCacheBackupRecovery(isbn, bookService::findByIsbn);
        if (bookFromStores.isPresent()) {
            log.trace("Book found in storages: {}", isbn);
            return bookFromStores.get();
        }

        GoogleBooksResponse.BookItem preview = googleBookService.searchByISBN(isbn)
                .items()
                .getFirst();
        log.debug("Book got from google api: {}", preview);
        return bookMapper.toBookDto(preview);
    }
}