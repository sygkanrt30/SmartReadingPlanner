package ru.yanin.practise.bookservice.service.book.import_book;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yanin.practise.bookservice.exception.BookNotFoundInApiException;
import ru.yanin.practise.bookservice.model.dto.BookDto;
import ru.yanin.practise.bookservice.model.entity.Book;
import ru.yanin.practise.bookservice.model.mapper.BookMapper;
import ru.yanin.practise.bookservice.model.mapper.BookMapperImpl;
import ru.yanin.practise.bookservice.model.mapper.ReadingPlanCalculationEventMapper;
import ru.yanin.practise.bookservice.service.book.BookService;
import ru.yanin.practise.bookservice.service.book.cache.CacheService;
import ru.yanin.practise.bookservice.service.book.external_book_api.BookApiService;
import ru.yanin.shared.message_broker.producer.Producer;
import ru.yanin.shared.genre.Genre;

import java.util.Optional;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class BookImportServiceImplTest {

    private BookMapper bookMapper;

    @Mock
    private BookService bookService;

    @Mock
    private BookApiService managementBookApiService;

    @Mock
    private CacheService<String, BookDto> cacheService;

    private BookImportServiceImpl bookImportService;

    @BeforeEach
    void setUp() {
        bookMapper = new BookMapperImpl();
        bookImportService = new BookImportServiceImpl(
                bookMapper,
                mock(ReadingPlanCalculationEventMapper.class),
                bookService,
                mock(Producer.class),
                managementBookApiService,
                cacheService);
    }

    @Test
    @DisplayName("Should return book dto when book is in db")
    void importBookByIsbn1() {
        var isbn = "123-456-7890";
        var userId = 12L;
        var book = Instancio.of(Book.class).set(field(Book::getIsbn), isbn)
                .create();
        when(bookService.findByIsbn(isbn)).thenReturn(Optional.of(book));
        BookDto bookDto = bookMapper.toBookDto(book);

        BookDto result = bookImportService.importBookByIsbn(isbn, userId);

        assertEquals(bookDto, result);
        assertSame(bookDto.pages(), result.pages());
        verify(cacheService).cache(eq(isbn), any());
        verify(cacheService, never()).get(eq(isbn));
        verify(bookService).tieBookToUser(eq(userId), eq(bookDto.bookId()));
    }

    @Test
    @DisplayName("Should return book dto when book isn't in db, but it's in cache")
    void importBookByIsbn2() {
        var isbn = "123-456-78320";
        var userId = 10L;
        var bookDto = Instancio.of(BookDto.class).set(field(BookDto::isbn), isbn)
                .create();
        when(bookService.findByIsbn(isbn)).thenReturn(Optional.empty());
        when(cacheService.get(isbn)).thenReturn(Optional.of(bookDto));

        BookDto result = bookImportService.importBookByIsbn(isbn, userId);

        assertEquals(bookDto, result);
        assertSame(bookDto.pages(), result.pages());
        verify(cacheService, never()).cache(eq(isbn), any());
        verify(bookService).save(eq(bookDto));
        verify(bookService).tieBookToUser(eq(userId), eq(bookDto.bookId()));
    }

    @Test
    @DisplayName("Should throw exception when db have some problem")
    void importBookByIsbn3() {
        var isbn = "123-456-78320";
        var userId = 10L;
        var bookDto = Instancio.of(BookDto.class).set(field(BookDto::isbn), isbn)
                .create();
        when(bookService.findByIsbn(isbn)).thenReturn(Optional.empty());
        when(cacheService.get(isbn)).thenReturn(Optional.of(bookDto));
        when(bookService.save(bookDto)).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> bookImportService.importBookByIsbn(isbn, userId));
        verify(cacheService, never()).cache(eq(isbn), any());
        verify(bookService, never()).tieBookToUser(eq(userId), eq(bookDto.bookId()));
    }

    @Test
    @DisplayName("Should return book dto when book isn't in storages")
    void importBookByIsbn4() {
        var isbn = "123-456-78320";
        var userId = 10L;
        var bookDto = Instancio.of(BookDto.class)
                .set(field(BookDto::isbn), isbn)
                .set(field(BookDto::genre), Genre.COMEDY)
                .create();
        when(bookService.findByIsbn(isbn)).thenReturn(Optional.empty());
        when(cacheService.get(isbn)).thenReturn(Optional.empty());
        when(managementBookApiService.searchByISBN(isbn)).thenReturn(Optional.of(bookDto));
        when(bookService.save(bookDto, userId)).thenReturn(bookDto);

        BookDto result = bookImportService.importBookByIsbn(isbn, userId);

        assertEquals(bookDto, result);
        assertEquals(Genre.COMEDY, result.genre());
        verify(cacheService).cache(eq(isbn), any());
        verify(bookService, never()).save(eq(bookDto));
    }

    @Test
    @DisplayName("Should throw exception when book is saving in db")
    void importBookByIsbn5() {
        var isbn = "123-456-78320";
        var userId = 10L;
        var bookDto = Instancio.create(BookDto.class);
        when(bookService.findByIsbn(isbn)).thenReturn(Optional.empty());
        when(cacheService.get(isbn)).thenReturn(Optional.empty());
        when(managementBookApiService.searchByISBN(isbn)).thenReturn(Optional.of(bookDto));
        when(bookService.save(bookDto, userId)).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> bookImportService.importBookByIsbn(isbn, userId));
        verify(cacheService, never()).cache(eq(isbn), any());
        verify(bookService, never()).save(eq(bookDto));
    }

    @Test
    @DisplayName("Should throw exception when book not found in apis")
    void importBookByIsbn6() {
        var isbn = "123-456-78320";
        var userId = 10L;
        when(bookService.findByIsbn(isbn)).thenReturn(Optional.empty());
        when(cacheService.get(isbn)).thenReturn(Optional.empty());
        when(managementBookApiService.searchByISBN(isbn)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundInApiException.class, () -> bookImportService.importBookByIsbn(isbn, userId));
        verify(cacheService, never()).cache(eq(isbn), any());
    }
}