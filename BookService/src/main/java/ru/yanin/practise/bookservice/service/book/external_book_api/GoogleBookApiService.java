package ru.yanin.practise.bookservice.service.book.external_book_api;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.yanin.practise.bookservice.model.dto.BookDto;
import ru.yanin.practise.bookservice.model.dto.api_response.GoogleBooksResponse;
import ru.yanin.practise.bookservice.model.mapper.BookMapper;

import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("unused")
@Service
@Slf4j
@Qualifier("googleBookApiService")
class GoogleBookApiService implements BookApiService {

    private final RestClient restClient;
    private final String apiKey;
    private final String fieldSet;
    private final BookMapper bookMapper;

    GoogleBookApiService(
            RestClient googleBooksRestClient,
            BookMapper bookMapper,
            @Value("${google.books.api.key}") String apiKey,
            @Value("${google.books.api.book}") String fieldSet) {

        this.restClient = googleBooksRestClient;
        this.apiKey = apiKey;
        this.fieldSet = fieldSet;
        this.bookMapper = bookMapper;
    }

    @Override
    @Retry(name = "${google.books.retry-name}", fallbackMethod = "fallback")
    public Optional<BookDto> searchByISBN(String isbn) {
        log.debug("find book by google book api");
        return getBookDtoFromResponse("isbn:", isbn);
    }

    private GoogleBooksResponse fallback(String isbn, Exception e) {
        log.warn("Fallback triggered for ISBN: {}, error: {}", isbn, e.getMessage());
        return null;
    }

    @Override
    @Retry(name = "${google.books.retry-name}", fallbackMethod = "fallback")
    public Optional<BookDto> searchByTitle(String title) {
        log.debug("find book by google book api");
        return getBookDtoFromResponse("intitle:", title);
    }

    private @NonNull Optional<BookDto> getBookDtoFromResponse(String x, String titleOrIsbn) {
        GoogleBooksResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("q", x + titleOrIsbn)
                        .queryParam("key", apiKey)
                        .queryParam("fields", fieldSet)
                        .queryParam("maxResults", 1)
                        .build())
                .retrieve()
                .body(GoogleBooksResponse.class);
        if (Objects.isNull(response) || response.items().isEmpty()) {
            log.warn("Book not found in google books api by: {}", titleOrIsbn);
            return Optional.empty();
        }
        GoogleBooksResponse.BookItem bookItem = response.items().getFirst();
        return Optional.of(bookMapper.toBookDto(bookItem));
    }
}
