package ru.yanin.practise.bookservice.repository.user_book;

import org.springframework.dao.DataAccessException;

public interface UserBookRepository {

    void tieBookToUser(Long userId, Long bookId);

    Long countRowByUserAndBookId(Long userId, Long bookId);

    /**
     * Removes the string consisting of bookId and userId.
     * @param userId the current authenticated user id
     * @param bookId the book id
     * @return the number of rows affected
     * @throws DataAccessException if there is any problem issuing the remove
     */
    int deleteBookFromUser(Long userId, Long bookId) throws DataAccessException;
}
