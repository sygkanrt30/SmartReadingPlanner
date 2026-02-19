package ru.yanin.practise.bookservice.controller.external;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yanin.practise.bookservice.service.book.user_book.UserBookService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/books")
public class UserBookController {

    private final UserBookService userBookService;

    @DeleteMapping("/remove-from-user")
    public ResponseEntity<?> removeFromUser(@RequestParam("book_id") Long bookId,
                                            @RequestHeader("X-User-ID") Long userId) {

        userBookService.removeBookFromUser(userId, bookId);
        return ResponseEntity.ok("Book has been removed successfully");
    }
}
