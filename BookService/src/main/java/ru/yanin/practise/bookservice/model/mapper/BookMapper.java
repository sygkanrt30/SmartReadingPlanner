package ru.yanin.practise.bookservice.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import ru.yanin.practise.bookservice.model.dto.AuthorDto;
import ru.yanin.practise.bookservice.model.dto.BookDto;
import ru.yanin.practise.bookservice.model.dto.api_response.GoogleBooksResponse;
import ru.yanin.practise.bookservice.model.dto.api_response.OpenLibrarySearchResponse;
import ru.yanin.practise.bookservice.model.entity.Book;
import ru.yanin.shared.genre.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookMapper {

    @Mapping(target = "isbn", expression = "java(bookItem.volumeInfo().getPreferredIsbn())")
    @Mapping(target = "title", source = "bookItem.volumeInfo.title")
    @Mapping(target = "pages", source = "bookItem.volumeInfo.pageCount")
    @Mapping(target = "publisher", source = "bookItem.volumeInfo.publisher")
    @Mapping(target = "publishedDate", expression = "java(bookItem.volumeInfo().getPublishedDateAsLocalDate())")
    @Mapping(target = "language", source = "bookItem.volumeInfo.language")
    @Mapping(target = "description", source = "bookItem.volumeInfo.description")
    @Mapping(target = "imageLink", source = "bookItem.volumeInfo.imageLinks.thumbnail")
    @Mapping(target = "genre", expression = "java(getGenre(bookItem.volumeInfo().categories()))")
    @Mapping(target = "authors", expression = "java(setStringToSetAuthorDto(bookItem.volumeInfo().authors()))")
    BookDto toBookDto(GoogleBooksResponse.BookItem bookItem);

    default Genre getGenre(List<String> categories) {
        for (String category : categories) {
            Genre genre = Genre.genreFrom(category);
            if (!genre.equals(Genre.NO_GENRE)){
                return genre;
            }
        }
        return Genre.NO_GENRE;
    }

    default Set<AuthorDto> setStringToSetAuthorDto(Collection<String> authors) {
        return authors.stream()
                .map(AuthorDto::new)
                .collect(Collectors.toSet());
    }

    @Mapping(target = "bookId", source = "book.id")
    BookDto toBookDto(Book book);

    @Mapping(target = "id", source = "bookDto.bookId")
    Book toBook(BookDto bookDto);

    @Mapping(target = "bookId", source = "id")
    BookDto toBookDtoWithNewId(BookDto bookDto, Long id);

    @Mapping(target = "isbn", expression = "java(edition.getPreferredIsbn())")
    @Mapping(target = "title", source = "work.title")
    @Mapping(target = "pages", source = "edition.pages")
    @Mapping(target = "publisher", source = "edition.publisher", qualifiedByName = "extractFirstPublisher")
    @Mapping(target = "publishedDate", expression = "java(edition.getPublishedDateAsLocalDate())")
    @Mapping(target = "language", source = "edition.language", qualifiedByName = "extractFirstLanguage")
    @Mapping(target = "genre", expression = "java(getGenre(work.genres()))")
    @Mapping(target = "description", source = "edition", qualifiedByName = "extractDescription")
    @Mapping(target = "averageRating", constant = "null")
    @Mapping(target = "imageLink", source = "edition.coverId", qualifiedByName = "buildImageLink")
    @Mapping(target = "authors", expression = "java(setStringToSetAuthorDto(work.authorName()))")
    BookDto toBookDto(OpenLibrarySearchResponse.OpenLibraryWork work,
                      OpenLibrarySearchResponse.OpenLibraryEdition edition);

    @Named("extractFirstPublisher")
    default String extractFirstPublisher(List<String> publishers) {
        if (publishers != null && !publishers.isEmpty()) {
            return publishers.getFirst();
        }
        return null;
    }

    @Named("extractFirstLanguage")
    default String extractFirstLanguage(List<String> languages) {
        if (languages != null && !languages.isEmpty()) {
            return languages.getFirst();
        }
        return null;
    }

    @Named("extractDescription")
    default String extractDescription(OpenLibrarySearchResponse.OpenLibraryEdition edition) {
        return switch (edition.description()) {
            case String s -> s;
            case Map<?, ?> m -> m.get("value") != null ? m.get("value").toString() : null;
            default -> null;
        };
    }

    @Named("buildImageLink")
    default String buildImageLink(Integer coverId) {
        return coverId != null
                ? String.format("https://covers.openlibrary.org/b/id/%d-L.jpg", coverId)
                : null;
    }
}
