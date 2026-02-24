package ru.yanin.practise.bookservice.service.book;

import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yanin.practise.bookservice.model.dto.BookDto;
import ru.yanin.practise.bookservice.model.entity.Book;
import ru.yanin.practise.bookservice.model.mapper.BookMapper;
import ru.yanin.practise.bookservice.repository.BookRepository;
import ru.yanin.practise.bookservice.service.book.cache.CacheService;

import java.util.List;
import java.util.Optional;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CacheService<String, BookDto> cacheService;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookServiceImpl bookServiceImpl;

    @Test
    @DisplayName("Should return list of book dto(2 from cache and 1 from db) if isbns are valid")
    void findAllByIsbnsWithCache1() {
        var isbns = List.of("123-456-7890", "123-456-9876", "123-456-123");
        var dtos = createDtoList(isbns);
        var book = Instancio.of(Book.class)
                .set(field(Book::getIsbn), isbns.get(1))
                .create();
        for (int i = 0; i < isbns.size(); i++) {
            if (i == 1) {
                when(cacheService.get(isbns.get(i))).thenReturn(Optional.empty());
                continue;
            }
            when(cacheService.get(isbns.get(i))).thenReturn(Optional.of(dtos.get(i)));
        }
        when(bookRepository.findByIsbn(isbns.get(1))).thenReturn(Optional.of(book));
        when(bookMapper.toBookDto(book)).thenReturn(dtos.get(1));

        List<BookDto> result = bookServiceImpl.findAllByIsbnsWithCache(isbns);

        verify(bookRepository, times(1)).findByIsbn(isbns.get(1));
        assertEquals(isbns.getFirst(), result.getFirst().isbn());
    }

    @Test
    @DisplayName("Should return list of book dto(all from cache) if isbns are valid")
    void findAllByIsbnsWithCache2() {
        var isbns = List.of("123-456-7890", "123-456-9876", "123-456-123");
        var dtos = createDtoList(isbns);
        for (int i = 0; i < isbns.size(); i++) {
            when(cacheService.get(isbns.get(i))).thenReturn(Optional.of(dtos.get(i)));
        }

        List<BookDto> result = bookServiceImpl.findAllByIsbnsWithCache(isbns);

        verify(bookRepository, never()).findByIsbn(anyString());
        assertEquals(isbns.getFirst(), result.getFirst().isbn());
        assertEquals(isbns.getLast(), result.getLast().isbn());
    }

    private List<BookDto> createDtoList(List<String> isbns) {
        return isbns.stream()
                .map(isbn -> Instancio.of(BookDto.class)
                        .set(field(BookDto::isbn), isbn)
                        .create())
                .toList();
    }

}