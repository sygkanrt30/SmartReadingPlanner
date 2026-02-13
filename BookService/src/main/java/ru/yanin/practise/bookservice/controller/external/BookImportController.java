package ru.yanin.practise.bookservice.controller.external;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yanin.practise.bookservice.model.dto.BookDto;
import ru.yanin.practise.bookservice.service.book.import_book.BookImportService;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/books/import")
@RequiredArgsConstructor
public class BookImportController {

    private final BookImportService importService;

    @PostMapping("/isbn/{isbn}")
    public ResponseEntity<BookDto> importBookByIsbn(@PathVariable String isbn,
                                                    @RequestHeader("X-User-ID") Long userId,
                                                    @RequestParam(defaultValue = "false") boolean analyze) {
        String cleanIsbn = isbn.replaceAll("[\\s-]", "");
        return ResponseEntity.ok(switch (analyze) {
            case true -> importService.importBookByIsbnAndSendEventForPlanning(cleanIsbn, userId);
            case false -> importService.importBookByIsbn(cleanIsbn, userId);
        });
    }

    @PostMapping("/title/{title}")
    public ResponseEntity<BookDto> importBookByTitle(@PathVariable String title,
                                                     @RequestHeader("X-User-ID") Long userId,
                                                     @RequestParam(defaultValue = "false") boolean analyze) {
        return ResponseEntity.ok(switch (analyze) {
            case true -> importService.importBookByTitleAndSendEventForPlanning(title, userId);
            case false -> importService.importBookByTitle(title, userId);
        });
    }

    @GetMapping("/preview/{isbn}")
    public ResponseEntity<BookDto> previewBook(@PathVariable String isbn) {
        String cleanIsbn = isbn.replaceAll("[\\s-]", "");
        BookDto response = importService.previewBook(cleanIsbn);

        if (Objects.isNull(response)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }
}
