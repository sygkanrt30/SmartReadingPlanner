package ru.yanin.practise.bookservice.service.book.user_book;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ru.yanin.practise.bookservice.model.dto.BookDto;
import ru.yanin.practise.bookservice.model.dto.sort_request.FieldNameToSortBy;
import ru.yanin.practise.bookservice.model.dto.sort_request.SortAndPaginationRequest;
import ru.yanin.practise.bookservice.repository.user_book.UserBookRepository;
import ru.yanin.practise.bookservice.service.book.BookService;
import ru.yanin.practise.bookservice.service.user_api.internal.InternalUserApiService;
import ru.yanin.shared.language.Language;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserBookServiceImpl implements UserBookService {

    private final UserBookRepository userBookRepository;
    private final BookService bookService;
    private final InternalUserApiService userServiceApi;

    @Override
    public void tieBookToUser(Long userId, Long bookId, String isbn) {
        Long rowCount = userBookRepository.countRowByUserAndBookId(userId, bookId);
        if (Objects.nonNull(rowCount) && rowCount > 0) {
            log.warn("book {} already tied to user {}", bookId, userId);
            return;
        }
        userBookRepository.tieBookToUser(userId, bookId, isbn);
        log.info("Book with id {}, tied to user id: {}", bookId, userId);
    }

    @Override
    public void removeBookFromUser(Long userId, Long bookId) {
        try {
            int numberOfAffectedRows = userBookRepository.deleteBookFromUser(userId, bookId);
            if (numberOfAffectedRows == 0) {
                log.warn("Such a pair of userId({}) and bookId({}) doesn't exist in db", userId, bookId);
                return;
            }
            log.debug("Removed book with id {} from user with id {}", bookId, userId);
        } catch (DataAccessException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    @Override
    public List<BookDto> findAllWithPagination(Long userId, int page, int size) {
        List<String> isbns = userBookRepository.findAllISBNByUserIdWithPagination(userId, page, size);
        if (isbns.isEmpty()) {
            log.warn("The user by id: {} has no added books", userId);
            return Collections.emptyList();
        }
        return bookService.findAllByIsbnsWithCache(isbns);
    }

    @Override
    public List<BookDto> findAllWithPaginationAndSort(Long userId, SortAndPaginationRequest sortRequest) {
        Language lang = getLangIfFieldToSortByIsTitle(userId, sortRequest);
        List<String> isbns = userBookRepository.findAllISBNByUserIdWithPaginationAndSort(userId, lang, sortRequest);
        if (isbns.isEmpty()) {
            log.warn("The user by id: {} has no added books", userId);
            return Collections.emptyList();
        }
        return bookService.findAllByIsbnsWithCache(isbns);
    }

    @Nullable
    private Language getLangIfFieldToSortByIsTitle(Long userId, SortAndPaginationRequest sortRequest) {
        if (sortRequest.fieldName().equals(FieldNameToSortBy.TITLE)) {
            return userServiceApi.getLanguage(userId);
        }
        return null;
    }
}
