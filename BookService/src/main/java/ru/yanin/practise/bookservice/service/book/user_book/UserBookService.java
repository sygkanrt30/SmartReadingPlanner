package ru.yanin.practise.bookservice.service.book.user_book;

public interface UserBookService {

    void tieBookToUser(Long userId, Long bookId);

    void removeBookFromUser(Long userId, Long bookId);
}
