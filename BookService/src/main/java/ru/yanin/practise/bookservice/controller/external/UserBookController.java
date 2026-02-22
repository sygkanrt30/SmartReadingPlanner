package ru.yanin.practise.bookservice.controller.external;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yanin.practise.bookservice.model.dto.BookDto;
import ru.yanin.practise.bookservice.model.dto.sort_request.SortAndPaginationRequest;
import ru.yanin.practise.bookservice.service.book.user_book.UserBookService;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@RestController
@RequestMapping("${spring.application.base-url}/user")
public class UserBookController {

    private final UserBookService userBookService;

    @DeleteMapping
    public ResponseEntity<?> removeFromUser(@RequestBody Long[] bookIds,
                                            @RequestHeader("X-User-ID") Long userId) {

        if (Objects.isNull(bookIds) || bookIds.length == 0){
            return ResponseEntity.accepted()
                    .body("The id array to be deleted isn't passed or is empty");
        }
        userBookService.removeBooksFromUser(userId, bookIds);
        return ResponseEntity.ok("Book has been removed successfully");
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllImportsToUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestHeader("X-User-ID") Long userId) {

        var dtos = userBookService.findAllWithPagination(userId, page, size);
        return getResponse(dtos);
    }

    private @NonNull ResponseEntity<?> getResponse(List<BookDto> dtos) {
        if (dtos.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body("User haven't been add books or user have been removed all books");
        }
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/all/sort")
    public ResponseEntity<?> getAllImportsToUserWithSort(
            @RequestBody SortAndPaginationRequest sortRequest,
            @RequestHeader("X-User-ID") Long userId) {

        var dtos = userBookService.findAllWithPaginationAndSort(userId, sortRequest);
        return getResponse(dtos);
    }
}