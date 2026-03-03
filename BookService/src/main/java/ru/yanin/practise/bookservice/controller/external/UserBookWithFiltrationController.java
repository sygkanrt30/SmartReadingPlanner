package ru.yanin.practise.bookservice.controller.external;

import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yanin.practise.bookservice.model.dto.BookDto;
import ru.yanin.practise.bookservice.model.dto.request.FilterAndSortRequest;
import ru.yanin.practise.bookservice.model.dto.request.filter.FilterRequest;
import ru.yanin.practise.bookservice.model.dto.request.sort.SortRequest;
import ru.yanin.practise.bookservice.service.book.user_book.UserBookService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("${spring.application.base-url}/user/filter")
public class UserBookWithFiltrationController {

    private final UserBookService userBookService;

    @GetMapping
    public ResponseEntity<?> getAllBooksToUserWithFilter(
            @RequestBody @Valid FilterRequest filterRequest,
            @RequestHeader("X-User-ID") Long userId) {

        var dtos = userBookService.findAllWithFiltering(userId, filterRequest);
        return getResponse(dtos);
    }

    @GetMapping("/sort")
    public ResponseEntity<?> getAllBooksToUserWithFilterAndSort(
            @RequestBody @Valid FilterAndSortRequest filterAndSortRequest,
            @RequestHeader("X-User-ID") Long userId) {

        var dtos = userBookService.findAllWithFiltering(userId, filterAndSortRequest);
        return getResponse(dtos);
    }

    @GetMapping("/by-favorite-genres")
    public ResponseEntity<?> getAllBooksToUserWithFilterByFavoriteGenres(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestHeader("X-User-ID") Long userId) {

        var dtos = userBookService.findAllByFavoriteGenres(userId, page, size);
        return getResponse(dtos);
    }

    @GetMapping("/by-favorite-genres/sort")
    public ResponseEntity<?> getAllBooksToUserWithFilterByFavoriteGenresAndSort(
            @RequestBody SortRequest sortRequest,
            @RequestHeader("X-User-ID") Long userId) {

        var dtos = userBookService.findAllByFavoriteGenres(userId, sortRequest);
        return getResponse(dtos);
    }

    private @NonNull ResponseEntity<?> getResponse(List<BookDto> dtos) {
        if (dtos.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body("User doesn't have books that match the parameters");
        }
        return ResponseEntity.ok(dtos);
    }
}
