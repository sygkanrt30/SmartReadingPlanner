package ru.yanin.practise.bookservice.service.book.import_book;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yanin.practise.bookservice.exception.BookNotFoundInApiException;
import ru.yanin.practise.bookservice.model.dto.BookDto;
import ru.yanin.practise.bookservice.model.dto.ReadingPlanCalculationEvent;
import ru.yanin.practise.bookservice.model.entity.Book;
import ru.yanin.practise.bookservice.model.mapper.BookMapper;
import ru.yanin.practise.bookservice.model.mapper.ReadingPlanCalculationEventMapper;
import ru.yanin.practise.bookservice.service.book.BookService;
import ru.yanin.practise.bookservice.service.book.cache.CacheService;
import ru.yanin.practise.bookservice.service.book.external_book_api.BookApiService;
import ru.yanin.practise.bookservice.service.book.user_book.UserBookService;
import ru.yanin.shared.message_broker.producer.Producer;

import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookImportServiceImpl implements BookImportService {

    private final @Qualifier("readingPlanCalculationEventMapperImpl") ReadingPlanCalculationEventMapper eventMapper;
    private final @Qualifier("managementBookApiService") BookApiService managementBookApiService;
    private final Producer<ReadingPlanCalculationEvent> eventProducer;
    private final CacheService<String, BookDto> cacheService;
    private final UserBookService userBookService;
    private final BookService bookService;
    private final BookMapper bookMapper;

    @Override
    public BookDto importBookByTitle(String title, Long userId) {
        Optional<BookDto> bookFromStores = findBookWithCacheBackupRecovery(title, bookService::findByTitle);
        return importBook(bookFromStores, title, userId, managementBookApiService::searchByTitle);
    }

    @Override
    public BookDto importBookByIsbn(String isbn, Long userId) {
        Optional<BookDto> bookFromStores = findBookWithCacheBackupRecovery(isbn, bookService::findByIsbn);
        return importBook(bookFromStores, isbn, userId, managementBookApiService::searchByISBN);
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
                               Function<String, Optional<BookDto>> getBookFromApi) {
        return switch (optionalBook.isPresent()) {
            case true -> {
                var book = optionalBook.get();
                log.debug("Book with id {} found in storages", book.bookId());
                userBookService.tieBookToUser(userId, book.bookId(), book.isbn());
                yield book;
            }
            case false -> importIfBookNotInStorages(identParam, userId, getBookFromApi);
        };
    }

    private BookDto importIfBookNotInStorages(String identParam, Long userId,
                                              Function<String, Optional<BookDto>> getBookFromApi) {

        log.debug("Book with {} not found in storages", identParam);
        Optional<BookDto> dtoOptional = getBookFromApi.apply(identParam);
        if (dtoOptional.isEmpty()) {
            throw new BookNotFoundInApiException("Book wasn't found by any of supported external book apis");
        }
        BookDto bookDto = bookService.save(dtoOptional.get());
        userBookService.tieBookToUser(userId, bookDto.bookId(), bookDto.isbn());
        cacheService.cache(identParam, bookDto);
        log.debug("Book saved with id {} in db and in stores", bookDto.bookId());
        return bookDto;
    }

    @Override
    public BookDto importBookByIsbnAndSendEventForPlanning(String isbn, Long userId) {
        var bookDto = importBookByIsbn(isbn, userId);
        convertAndSendEvent(userId, bookDto);
        return bookDto;
    }

    private void convertAndSendEvent(Long userId, BookDto bookDto) {
        ReadingPlanCalculationEvent event = eventMapper.toEvent(bookDto, userId);
        eventProducer.send(event);
    }

    @Override
    public BookDto importBookByTitleAndSendEventForPlanning(String bookName, Long userId) {
        var bookDto = importBookByTitle(bookName, userId);
        convertAndSendEvent(userId, bookDto);
        return bookDto;
    }

    @Override
    public BookDto previewBook(String isbn) {
        Optional<BookDto> bookFromStores = findBookWithCacheBackupRecovery(isbn, bookService::findByIsbn);
        if (bookFromStores.isPresent()) {
            log.trace("Book found in storages: {}", isbn);
            return bookFromStores.get();
        }
        return managementBookApiService.searchByISBN(isbn).orElseThrow(() ->
                        new BookNotFoundInApiException("Book wasn't found by any of supported external book apis"));
    }
}