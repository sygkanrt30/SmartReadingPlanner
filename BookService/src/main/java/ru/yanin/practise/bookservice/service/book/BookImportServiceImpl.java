package ru.yanin.practise.bookservice.service.book;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;
import ru.yanin.practise.bookservice.model.dto.BookDto;
import ru.yanin.practise.bookservice.model.dto.GoogleBooksResponse;
import ru.yanin.practise.bookservice.model.dto.mapper.AuthorMapper;
import ru.yanin.practise.bookservice.model.dto.mapper.BookMapper;
import ru.yanin.practise.bookservice.model.entity.Author;
import ru.yanin.practise.bookservice.model.entity.Book;
import ru.yanin.practise.bookservice.repository.BookRepository;
import ru.yanin.practise.bookservice.service.book.cache.CacheService;
import ru.yanin.practise.bookservice.service.book.google_book.GoogleBookService;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j

//todo тесты на этот класс
public class BookImportServiceImpl implements BookImportService {

    private final BookMapper bookMapper;
    private final AuthorMapper authorMapper;
    private final BookRepository bookRepository;
    private final GoogleBookService googleBookService;
    private final SimpleJdbcInsert userBookJdbcInsert;
    private final CacheService<String, Book> cacheService;

    @Override
    public BookDto importBookByIsbn(String isbn, Long userId) {
        Optional<Book> optionalBook = findBookInStorages(isbn, bookRepository::findByIsbn);
        if (optionalBook.isEmpty()) {
            return importBookIfBookNotInStorages(isbn, userId);
        }

        log.debug("Book with ISBN {} found in storages", isbn);
        Book book = optionalBook.get();
        tieBookToUser(userId, book.getId());
        return bookMapper.toBookDto(book);
    }

    private Optional<Book> findBookInStorages(String bookIsbnOrName,
                                              Function<String, Optional<Book>> function) {
        Optional<Book> book = cacheService.get(bookIsbnOrName);
        if (book.isPresent()) {
            log.debug("Book found in cache: {}", bookIsbnOrName);
            return book;
        }
        return function.apply(bookIsbnOrName);
    }

    private BookDto importBookIfBookNotInStorages(String isbnOrName, Long userId) {
        log.trace("Book with {} not found in storages", isbnOrName);
        GoogleBooksResponse.BookItem bookItem = googleBookService.searchByBookName(isbnOrName)
                .items()
                .getFirst();

        Book savedBook = saveBook(bookItem);
        Long bookId = savedBook.getId();
        log.debug("Book with {} saved, id: {}", isbnOrName, bookId);
        tieBookToUser(userId, bookId);

        cacheService.cache(isbnOrName, savedBook);
        log.debug("Book with {} cached", isbnOrName);
        return bookMapper.toBookDto(savedBook);
    }

    private Book saveBook(GoogleBooksResponse.BookItem bookItem) {
        Set<Author> authors = bookItem.volumeInfo()
                .authors()
                .stream()
                .map(authorMapper::toAuthor)
                .collect(Collectors.toSet());

        Book book = bookMapper.toBook(bookItem);

        authors.forEach(author -> {
            author.addBook(book);
            book.addAuthor(author);
        });

        return bookRepository.save(book);
    }

    private void tieBookToUser(Long userId, Long bookId) {
        userBookJdbcInsert.execute(Map.of(
                "user_id", userId,
                "book_id", bookId));

        log.info("Book with id {}, tied to user id: {}", bookId, userId);
    }

    @Override
    public BookDto importBookByName(String bookName, Long userId) {
        Optional<Book> optionalBook = findBookInStorages(bookName, bookRepository::findByTitle);
        if (optionalBook.isEmpty()) {
            return importBookIfBookNotInStorages(bookName, userId);
        }

        log.debug("Book with name {} found in storages", bookName);
        Book book = optionalBook.get();
        tieBookToUser(userId, book.getId());
        return bookMapper.toBookDto(book);
    }

    @Override
    public BookDto previewBook(String isbn) {
        Optional<Book> book = findBookInStorages(isbn, bookRepository::findByIsbn);
        if (book.isPresent()) {
            log.debug("Book found in storages: {}", isbn);
            return bookMapper.toBookDto(book.get());
        }

        GoogleBooksResponse.BookItem preview = googleBookService.searchByISBN(isbn)
                .items()
                .getFirst();

        log.debug("Book got from google api: {}", preview);
        return bookMapper.toBookDto(preview);
    }
}
