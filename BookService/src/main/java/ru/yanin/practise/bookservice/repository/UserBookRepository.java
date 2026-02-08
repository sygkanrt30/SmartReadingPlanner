package ru.yanin.practise.bookservice.repository;

public interface UserBookRepository {

    void tieBookToUser(Long userId, Long bookId);

    Long countRowByUserAndBookId(Long userId, Long bookId);
}
