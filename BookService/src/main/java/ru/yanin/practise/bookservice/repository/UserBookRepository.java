package ru.yanin.practise.bookservice.repository;

import org.springframework.dao.DataAccessException;
import ru.yanin.practise.bookservice.model.dto.sort_request.SortAndPaginationRequest;
import ru.yanin.shared.language.Language;

import java.util.List;

public interface UserBookRepository {

    void tieBookToUser(Long userId, Long bookId, String isbn);

    Long countRowByUserAndBookId(Long userId, Long bookId);

    /**
     * Removes the string consisting of bookId and userId.
     * @param userId the current authenticated user id
     * @param bookId the book id
     * @return the number of rows affected
     * @throws DataAccessException if there is any problem issuing the remove
     */
    int deleteBooksFromUser(Long userId, Long... bookId) throws DataAccessException;

    List<String> findAllISBNByUserIdWithPagination(Long userId, int page, int size);

    List<String> findAllISBNByUserIdWithPaginationAndSort(Long userId, Language lang,
                                                          SortAndPaginationRequest sortRequest);
}
