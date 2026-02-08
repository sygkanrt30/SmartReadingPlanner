package ru.yanin.practise.bookservice.service.book.google_book;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;
import ru.yanin.practise.bookservice.model.dto.GoogleBooksResponse;

import java.net.URI;

@SuppressWarnings("unused")
@Service
@Slf4j
public class GoogleBookServiceImpl implements GoogleBookService {

    private final RestClient restClient;
    private final String apiKey;
    private final String fieldSet;

    public GoogleBookServiceImpl(
            RestClient googleBooksRestClient,
            @Value("${google.books.api.key}") String apiKey,
            @Value("${google.books.api.book}") String fieldSet) {

        this.restClient = googleBooksRestClient;
        this.apiKey = apiKey;
        this.fieldSet = fieldSet;
    }

    @Override
    @Retry(name = "${google.books.retry-name}", fallbackMethod = "fallback")
    public GoogleBooksResponse searchByISBN(String isbn) {
        log.info("find book by google book api");
        return restClient.get()
                .uri(uriBuilder ->
                        createUriWithQueryParams(uriBuilder, "isbn:" + isbn))
                .retrieve()
                .body(GoogleBooksResponse.class);
    }

    private GoogleBooksResponse fallback(String isbn, Exception e) {
        log.warn("Fallback triggered for ISBN: {}, error: {}", isbn, e.getMessage());
        return null;
    }

    @Override
    @Retry(name = "${google.books.retry-name}", fallbackMethod = "fallback")
    public GoogleBooksResponse searchByName(String bookName) {
        return restClient.get()
                .uri(uriBuilder ->
                        createUriWithQueryParams(uriBuilder, "intitle:" + bookName))
                .retrieve()
                .body(GoogleBooksResponse.class);
    }

    private URI createUriWithQueryParams(UriBuilder uriBuilder, String queryValue) {
        return uriBuilder.queryParam("q", queryValue)
                .queryParam("key", apiKey)
                .queryParam("fields", fieldSet)
                .queryParam("maxResults", 1)
                .build();
    }
}
