package ru.yanin.practise.bookservice.service.book;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yanin.practise.bookservice.model.dto.BookDto;
import ru.yanin.practise.bookservice.model.entity.Book;
import ru.yanin.practise.bookservice.model.mapper.BookMapper;
import ru.yanin.practise.bookservice.repository.BookRepository;
import ru.yanin.practise.bookservice.service.book.cache.CacheService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final CacheService<String, BookDto> cacheService;
    private final BookMapper bookMapper;

    @Override
    public BookDto save(BookDto bookDto) {
        Book savedBook = bookRepository.save(bookMapper.toBook(bookDto));
        log.trace("Saved book: {}", savedBook);
        return bookMapper.toBookDto(savedBook);
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    @Override
    public Optional<Book> findByTitle(String title) {
        return bookRepository.findByTitle(title);
    }

    @Override
    public List<BookDto> findAllByIsbnsWithCache(List<String> isbns) {
        var result = new ArrayList<BookDto>();
        for (String isbn : isbns) {
            var dtoOptional = cacheService.get(isbn);
            if (dtoOptional.isPresent()) {
                result.add(dtoOptional.get());
                continue;
            }
            var bookOptional = bookRepository.findByIsbn(isbn);
            if (bookOptional.isPresent()) {
                var bookDto = bookMapper.toBookDto(bookOptional.get());
                result.add(bookDto);
                cacheService.cache(bookDto.isbn(), bookDto);
            }
        }
        if (result.size() != isbns.size()) {
            log.error("""
                    The number of transferred isbn and the number of books found from them don't match.
                    Number of books: {}; number of isbn: {}""",
                    result.size(), isbns.size());
        }
        return result;
    }
}
