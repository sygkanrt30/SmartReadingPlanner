package ru.yanin.practise.bookservice.service.book.user_book;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ru.yanin.practise.bookservice.model.dto.BookDto;
import ru.yanin.practise.bookservice.model.dto.request.FilterAndSortRequest;
import ru.yanin.practise.bookservice.model.dto.request.filter.FilterRequest;
import ru.yanin.practise.bookservice.model.dto.request.sort.FieldNameToSortBy;
import ru.yanin.practise.bookservice.model.dto.request.sort.SortRequest;
import ru.yanin.practise.bookservice.repository.UserBookRepository;
import ru.yanin.practise.bookservice.service.user_api.internal.InternalUserApiService;
import ru.yanin.shared.genre.Genre;
import ru.yanin.shared.language.Language;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserBookServiceImpl implements UserBookService {

    private final UserBookRepository userBookRepository;
    private final InternalUserApiService userServiceApi;

    @Override
    public void tieBookToUser(Long userId, Long bookId) {
        Long rowCount = userBookRepository.countRowByUserAndBookId(userId, bookId);
        if (Objects.nonNull(rowCount) && rowCount > 0) {
            log.warn("book {} already tied to user {}", bookId, userId);
            return;
        }
        userBookRepository.tieBookToUser(userId, bookId);
        log.info("Book with id {}, tied to user id: {}", bookId, userId);
    }

    @Override
    public void removeBooksFromUser(Long userId, long... bookIds) {
        try {
            int numberOfAffectedRows = userBookRepository.deleteBooksFromUser(userId, bookIds);
            if (numberOfAffectedRows == 0) {
                log.warn("Such a pair of userId({}) and bookId({}) doesn't exist in db", userId, bookIds);
                return;
            }
            log.debug("Removed book with id {} from user with id {}", bookIds, userId);
        } catch (DataAccessException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    @Override
    public List<BookDto> findAll(Long userId, int page, int size) {
        return userBookRepository.findAllByUserId(userId, page, size);
    }

    @Override
    public List<BookDto> findAll(Long userId, SortRequest sortRequest) {
        Language lang = getLangIfFieldToSortByIsTitle(userId, sortRequest.fieldName());
        return userBookRepository.findAllByUserId(userId, lang, sortRequest);
    }

    @Nullable
    private Language getLangIfFieldToSortByIsTitle(Long userId, FieldNameToSortBy fieldNameToSortBy) {
        if (fieldNameToSortBy.equals(FieldNameToSortBy.TITLE)) {
            return userServiceApi.getLanguage(userId);
        }
        return null;
    }

    @Override
    public List<BookDto> findAllWithFiltering(Long userId, FilterRequest filterRequest) {
        return userBookRepository.findAllWithFiltering(userId, filterRequest);
    }

    @Override
    public List<BookDto> findAllWithFiltering(Long userId, FilterAndSortRequest filterAndSortRequest) {
        Language lang = getLangIfFieldToSortByIsTitle(userId, filterAndSortRequest.fieldName());
        return userBookRepository.findAllWithFiltering(userId, filterAndSortRequest, lang);
    }

    @Override
    public List<BookDto> findAllByFavoriteGenres(Long userId, int page, int size) {
        Set<Genre> favoriteGenres = getGenresWithoutNoGenreStub(userId);
        if (favoriteGenres.isEmpty()) {
            log.warn("No favorite genres found for user {}", userId);
            return findAll(userId, page, size);
        }
        return userBookRepository.findAllByUserIdAndFavoriteGenres(userId, favoriteGenres, page, size);
    }

    private Set<Genre> getGenresWithoutNoGenreStub(Long userId) {
        return userServiceApi.getFavoriteGenre(userId).stream()
                .filter(genre -> !genre.equals(Genre.NO_GENRE))
                .collect(Collectors.toSet());
    }

    @Override
    public List<BookDto> findAllByFavoriteGenres(Long userId, SortRequest sortRequest) {
        Set<Genre> favoriteGenres = getGenresWithoutNoGenreStub(userId);
        Language lang = getLangIfFieldToSortByIsTitle(userId, sortRequest.fieldName());
        if (favoriteGenres.isEmpty()) {
            log.warn("No favorite genres found for user {}", userId);
            return userBookRepository.findAllByUserId(userId, lang, sortRequest);
        }
        return userBookRepository.findAllByUserIdAndFavoriteGenres(userId, favoriteGenres, lang, sortRequest);
    }
}
