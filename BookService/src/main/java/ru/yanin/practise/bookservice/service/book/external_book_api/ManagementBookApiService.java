package ru.yanin.practise.bookservice.service.book.external_book_api;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yanin.practise.bookservice.model.dto.BookDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@Qualifier("managementBookApiService")
public final class ManagementBookApiService implements BookApiService {

    private final List<BookApiService> bookApiServices;

    @Autowired
    public ManagementBookApiService(@Qualifier("googleBookApiService") BookApiService googleBookApiService,
                                    BookApiService... bookApiServices) {
        this.bookApiServices = new ArrayList<>();
        this.bookApiServices.addFirst(googleBookApiService);
        this.bookApiServices.addAll(Arrays.asList(bookApiServices));
    }

    @Override
    public Optional<BookDto> searchByISBN(String isbn) {
        var searchFunctions = bookApiServices.stream()
                .map(apiService -> (Function<String, Optional<BookDto>>) (apiService::searchByISBN))
                .toList();
        return searchInAllApi(isbn, searchFunctions);
    }

    private @NonNull Optional<BookDto> searchInAllApi(String isbnOrTitle,
                                                      List<Function<String, Optional<BookDto>>> searchFunctions) {
        for (var apiService : searchFunctions) {
            var dtoOptional = apiService.apply(isbnOrTitle);
            if (dtoOptional.isPresent()) {
                return dtoOptional;
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<BookDto> searchByTitle(String title) {
        var searchFunctions = bookApiServices.stream()
                .map(apiService -> (Function<String, Optional<BookDto>>) (apiService::searchByTitle))
                .toList();
        return searchInAllApi(title, searchFunctions);
    }
}
