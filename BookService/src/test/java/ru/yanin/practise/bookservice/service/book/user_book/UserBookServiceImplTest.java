package ru.yanin.practise.bookservice.service.book.user_book;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import ru.yanin.practise.bookservice.model.dto.BookDto;
import ru.yanin.practise.bookservice.model.dto.request.sort.FieldNameToSortBy;
import ru.yanin.practise.bookservice.model.dto.request.sort.SortRequest;
import ru.yanin.practise.bookservice.repository.UserBookRepository;
import ru.yanin.practise.bookservice.service.user_api.internal.InternalUserApiService;
import ru.yanin.shared.genre.Genre;
import ru.yanin.shared.language.Language;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class UserBookServiceImplTest {

    @Mock
    private UserBookRepository userBookRepository;

    @Mock
    private InternalUserApiService userApiService;

    @InjectMocks
    private UserBookServiceImpl userBookServiceImpl;

    private final Long userId = Instancio.create(Long.class);
    private final Long bookId = Instancio.create(Long.class);

    @Test
    void tieBookToUser_WhenBookNotTied_ShouldTieBook() {
        when(userBookRepository.countRowByUserAndBookId(userId, bookId)).thenReturn(0L);

        userBookServiceImpl.tieBookToUser(userId, bookId);

        verify(userBookRepository).countRowByUserAndBookId(userId, bookId);
        verify(userBookRepository).tieBookToUser(userId, bookId);
    }

    @Test
    void tieBookToUser_WhenBookAlreadyTied_ShouldNotTieBook() {
        when(userBookRepository.countRowByUserAndBookId(userId, bookId)).thenReturn(1L);

        userBookServiceImpl.tieBookToUser(userId, bookId);

        verify(userBookRepository).countRowByUserAndBookId(userId, bookId);
        verify(userBookRepository, never()).tieBookToUser(anyLong(), anyLong());
    }

    @Test
    void tieBookToUser_WhenCountIsNull_ShouldTieBook() {
        when(userBookRepository.countRowByUserAndBookId(userId, bookId)).thenReturn(null);

        userBookServiceImpl.tieBookToUser(userId, bookId);

        verify(userBookRepository).tieBookToUser(userId, bookId);
    }

    @Test
    void removeBooksFromUser_WhenBooksExist_ShouldRemoveBooks() {
        long[] bookIds = new long[]{75,0};
        when(userBookRepository.deleteBooksFromUser(userId, bookIds)).thenReturn(bookIds.length);

        userBookServiceImpl.removeBooksFromUser(userId, bookIds);

        verify(userBookRepository).deleteBooksFromUser(userId, bookIds);
    }

    @Test
    void removeBooksFromUser_WhenBooksNotExist_ShouldLogWarning() {
        long[] bookIds = new long[]{745};
        when(userBookRepository.deleteBooksFromUser(userId, bookIds)).thenReturn(0);

        userBookServiceImpl.removeBooksFromUser(userId, bookIds);

        verify(userBookRepository).deleteBooksFromUser(userId, bookIds);
    }

    @Test
    void removeBooksFromUser_WhenDataAccessException_ShouldThrowIllegalArgumentException() {
        long[] bookIds = new long[]{545};
        when(userBookRepository.deleteBooksFromUser(userId, bookIds))
                .thenThrow(new DataAccessException("DB error") {
                });

        assertThrows(IllegalArgumentException.class,
                () -> userBookServiceImpl.removeBooksFromUser(userId, bookIds));
    }

    @Test
    void findAllByFavoriteGenres_WithPaginationAndGenres_ShouldReturnBooksByGenres() {
        int page = Instancio.create(int.class);
        int size = Instancio.create(int.class);

        Set<Genre> favoriteGenres = Set.of(Genre.CHILDREN, Genre.SCIENCE);
        List<BookDto> expectedBooks = Instancio.ofList(BookDto.class).size(3).create();
        when(userApiService.getFavoriteGenre(userId)).thenReturn(favoriteGenres);
        when(userBookRepository.findAllByUserIdAndFavoriteGenres(userId, favoriteGenres, page, size))
                .thenReturn(expectedBooks);

        List<BookDto> result = userBookServiceImpl.findAllByFavoriteGenres(userId, page, size);

        assertEquals(expectedBooks, result);
        verify(userApiService).getFavoriteGenre(userId);
        verify(userBookRepository).findAllByUserIdAndFavoriteGenres(userId, favoriteGenres, page, size);
    }

    @Test
    void findAllByFavoriteGenres_WithPaginationAndOnlyNoGenre_ShouldReturnAllBooks() {
        int page = Instancio.create(int.class);
        int size = Instancio.create(int.class);
        Set<Genre> favoriteGenres = Set.of(Genre.NO_GENRE);
        List<BookDto> expectedBooks = Instancio.ofList(BookDto.class).size(3).create();
        when(userApiService.getFavoriteGenre(userId)).thenReturn(favoriteGenres);
        when(userBookRepository.findAllByUserId(userId, page, size)).thenReturn(expectedBooks);

        List<BookDto> result = userBookServiceImpl.findAllByFavoriteGenres(userId, page, size);

        assertEquals(expectedBooks, result);
        verify(userApiService).getFavoriteGenre(userId);
        verify(userBookRepository).findAllByUserId(userId, page, size);
        verify(userBookRepository, never()).findAllByUserIdAndFavoriteGenres(anyLong(), anySet(), anyInt(), anyInt());
    }

    @Test
    void findAllByFavoriteGenres_WithPaginationAndNoGenres_ShouldReturnAllBooks() {
        int page = Instancio.create(int.class);
        int size = Instancio.create(int.class);
        List<BookDto> expectedBooks = Instancio.ofList(BookDto.class).size(3).create();
        when(userApiService.getFavoriteGenre(userId)).thenReturn(new HashSet<>());
        when(userBookRepository.findAllByUserId(userId, page, size)).thenReturn(expectedBooks);

        List<BookDto> result = userBookServiceImpl.findAllByFavoriteGenres(userId, page, size);

        assertEquals(expectedBooks, result);
        verify(userApiService).getFavoriteGenre(userId);
        verify(userBookRepository).findAllByUserId(userId, page, size);
    }

    @Test
    void findAllByFavoriteGenres_WithSortByTitleAndGenres_ShouldReturnBooksByGenres() {
        SortRequest sortRequest = Instancio.of(SortRequest.class)
                .set(field(SortRequest::fieldName), FieldNameToSortBy.TITLE)
                .create();
        Set<Genre> favoriteGenres = Set.of(Genre.ADVENTURE, Genre.ROMANCE);
        Language language = Instancio.create(Language.class);
        List<BookDto> expectedBooks = Instancio.ofList(BookDto.class).size(2).create();
        when(userApiService.getFavoriteGenre(userId)).thenReturn(favoriteGenres);
        when(userApiService.getLanguage(userId)).thenReturn(language);
        when(userBookRepository.findAllByUserIdAndFavoriteGenres(userId, favoriteGenres, language, sortRequest))
                .thenReturn(expectedBooks);

        List<BookDto> result = userBookServiceImpl.findAllByFavoriteGenres(userId, sortRequest);

        assertEquals(expectedBooks, result);
        verify(userApiService).getFavoriteGenre(userId);
        verify(userApiService).getLanguage(userId);
        verify(userBookRepository).findAllByUserIdAndFavoriteGenres(userId, favoriteGenres, language, sortRequest);
    }

    @Test
    void findAllByFavoriteGenres_WithSortByPagesAndGenres_ShouldReturnBooksByGenres() {
        SortRequest sortRequest = Instancio.of(SortRequest.class)
                .set(field(SortRequest::fieldName), FieldNameToSortBy.PAGES)
                .create();
        Set<Genre> favoriteGenres = Set.of(Genre.CRIME, Genre.CLASSIC);
        List<BookDto> expectedBooks = Instancio.ofList(BookDto.class).size(2).create();
        when(userApiService.getFavoriteGenre(userId)).thenReturn(favoriteGenres);
        when(userBookRepository.findAllByUserIdAndFavoriteGenres(userId, favoriteGenres, null, sortRequest))
                .thenReturn(expectedBooks);

        List<BookDto> result = userBookServiceImpl.findAllByFavoriteGenres(userId, sortRequest);

        assertEquals(expectedBooks, result);
        verify(userApiService).getFavoriteGenre(userId);
        verify(userApiService, never()).getLanguage(anyLong());
        verify(userBookRepository).findAllByUserIdAndFavoriteGenres(userId, favoriteGenres, null, sortRequest);
    }

    @Test
    void findAllByFavoriteGenres_WithSortByTitleAndOnlyNoGenre_ShouldReturnAllBooks() {
        SortRequest sortRequest = Instancio.of(SortRequest.class)
                .set(field(SortRequest::fieldName), FieldNameToSortBy.TITLE)
                .create();
        Set<Genre> favoriteGenres = Set.of(Genre.NO_GENRE);
        Language language = Instancio.create(Language.class);
        List<BookDto> expectedBooks = Instancio.ofList(BookDto.class).size(2).create();
        when(userApiService.getFavoriteGenre(userId)).thenReturn(favoriteGenres);
        when(userApiService.getLanguage(userId)).thenReturn(language);
        when(userBookRepository.findAllByUserId(userId, language, sortRequest)).thenReturn(expectedBooks);

        List<BookDto> result = userBookServiceImpl.findAllByFavoriteGenres(userId, sortRequest);

        assertEquals(expectedBooks, result);
        verify(userApiService).getFavoriteGenre(userId);
        verify(userApiService).getLanguage(userId);
        verify(userBookRepository).findAllByUserId(userId, language, sortRequest);
    }

    @Test
    void findAllByFavoriteGenres_WithSortByPagesAndNoGenres_ShouldReturnAllBooks() {
        SortRequest sortRequest = Instancio.of(SortRequest.class)
                .set(field(SortRequest::fieldName), FieldNameToSortBy.PAGES)
                .create();
        List<BookDto> expectedBooks = Instancio.ofList(BookDto.class).size(2).create();
        when(userApiService.getFavoriteGenre(userId)).thenReturn(new HashSet<>());
        when(userBookRepository.findAllByUserId(userId, null, sortRequest)).thenReturn(expectedBooks);

        List<BookDto> result = userBookServiceImpl.findAllByFavoriteGenres(userId, sortRequest);

        assertEquals(expectedBooks, result);
        verify(userApiService).getFavoriteGenre(userId);
        verify(userBookRepository).findAllByUserId(userId, null, sortRequest);
    }
}