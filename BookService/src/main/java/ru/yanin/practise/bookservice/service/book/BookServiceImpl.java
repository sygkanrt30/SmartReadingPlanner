package ru.yanin.practise.bookservice.service.book;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yanin.practise.bookservice.model.dto.BookDto;
import ru.yanin.practise.bookservice.model.entity.Book;
import ru.yanin.practise.bookservice.model.mapper.BookMapper;
import ru.yanin.practise.bookservice.repository.BookRepository;
import ru.yanin.practise.bookservice.service.book.user_book.UserBookService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final UserBookService userBookService;

    @Override
    public Book save(BookDto bookDto) {
        Book savedBook = bookRepository.save(bookMapper.toBook(bookDto));
        log.trace("Saved book: {}", savedBook);
        return savedBook;
    }

    @Override
    public BookDto save(BookDto bookDto, Long userId) {
        Book savedBook = save(bookDto);
        userBookService.tieBookToUser(userId, savedBook.getId());
        return bookMapper.toBookDtoWithNewId(bookDto, savedBook.getId());
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    @Override
    public Optional<Book> findByTitle(String title) {
        return bookRepository.findByTitle(title);
    }
}
