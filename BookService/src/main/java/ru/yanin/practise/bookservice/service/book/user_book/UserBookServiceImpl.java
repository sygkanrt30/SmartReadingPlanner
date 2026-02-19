package ru.yanin.practise.bookservice.service.book.user_book;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import ru.yanin.practise.bookservice.repository.user_book.UserBookRepository;

import java.util.Objects;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserBookServiceImpl implements UserBookService {

    private final UserBookRepository userBookRepository;

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
    public void removeBookFromUser(Long userId, Long bookId) {
        try {
            int numberOfAffectedRows = userBookRepository.deleteBookFromUser(userId, bookId);
            if (numberOfAffectedRows == 0) {
                log.warn("Such a pair of userId({}) and bookId({}) doesn't exist in db", userId, bookId);
                return;
            }
            log.debug("Removed book with id {} from user with id {}", bookId, userId);
        } catch (DataAccessException e){
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }
}
