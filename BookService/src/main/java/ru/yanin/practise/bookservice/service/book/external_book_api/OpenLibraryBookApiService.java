package ru.yanin.practise.bookservice.service.book.external_book_api;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;
import ru.yanin.practise.bookservice.model.dto.BookDto;
import ru.yanin.practise.bookservice.model.dto.OpenLibrarySearchResponse;
import ru.yanin.practise.bookservice.model.mapper.BookMapper;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("unused")
@Service
@Slf4j
@Qualifier("openLibraryBookApiService")
class OpenLibraryBookApiService implements BookApiService {

    private final RestClient restClient;
    private final BookMapper bookMapper;
    private final String editionsFields;

    OpenLibraryBookApiService(
            @Qualifier("openLibraryApi") RestClient restClient,
            BookMapper bookMapper,
            @Value("${open-library.search.editions-fields}") String editionsFields) {
        this.restClient = restClient;
        this.bookMapper = bookMapper;
        this.editionsFields = editionsFields;
    }

    @Override
    @Retry(name = "${openlibrary.retry-name}", fallbackMethod = "fallback")
    public Optional<BookDto> searchByISBN(String isbn) {
        log.debug("Searching book in OpenLibrary by ISBN: {}", isbn);
        var response = executeSearch(isbn);
        return getAndMap2DtoIfResponseNotEmpty(response, isbn);
    }

    @Override
    @Retry(name = "${openlibrary.retry-name}", fallbackMethod = "fallback")
    public Optional<BookDto> searchByTitle(String title) {
        log.debug("Searching book in OpenLibrary by title: {}", title);
        var response = executeSearch(title.replace(" ", "+"));
        return getAndMap2DtoIfResponseNotEmpty(response, title);
    }

    private OpenLibrarySearchResponse executeSearch(String queryValue) {
        return restClient.get()
                .uri(uriBuilder -> createUriWithQueryParams(uriBuilder, queryValue))
                .retrieve()
                .body(OpenLibrarySearchResponse.class);
    }

    private URI createUriWithQueryParams(UriBuilder uriBuilder, String queryValue) {
        return uriBuilder.queryParam("q", queryValue)
                .queryParam("fields", "*")
                .queryParam("editions", "true")
                .queryParam("editions.fields", editionsFields)
                .queryParam("limit", "1")
                .build();
    }

    private @NonNull Optional<BookDto> getAndMap2DtoIfResponseNotEmpty(OpenLibrarySearchResponse response,
                                                                       String isbnOrTitle) {
        if (Objects.isNull(response) || response.numFound() == 0 ||
                Objects.isNull(response.docs()) || response.docs().isEmpty()) {
            log.warn("No book found in OpenLibrary for: {}", isbnOrTitle);
            return Optional.empty();
        }
        var openLibraryWork = response.docs().getFirst();
        var openLibraryEdition = getTopEdition(openLibraryWork);
        return Optional.of(bookMapper.toBookDto(openLibraryWork, openLibraryEdition));
    }

    private OpenLibrarySearchResponse.OpenLibraryEdition getTopEdition(
            OpenLibrarySearchResponse.OpenLibraryWork work) {

        if (Objects.nonNull(work.editions()) && Objects.nonNull(work.editions().editions())) {
            return work.editions().editions().getFirst();
        }
        return null;
    }

    private Optional<BookDto> fallback(String query, Exception e) {
        log.warn("OpenLibrary fallback triggered for query: {}, error: {}",
                query, Objects.nonNull(e.getMessage()) ? e.getMessage() : "Unknown error");
        return Optional.empty();
    }
}