package ru.yanin.practise.bookservice.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;
import ru.yanin.practise.bookservice.model.dto.AuthorDto;
import ru.yanin.practise.bookservice.model.dto.BookDto;
import ru.yanin.practise.bookservice.model.dto.GoogleBooksResponse;
import ru.yanin.practise.bookservice.model.entity.Book;
import ru.yanin.shared.genre.Genre;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookMapper {

    @Mapping(target = "isbn", expression = "java(bookItem.volumeInfo().getIsbn13())")
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

    default Set<AuthorDto> setStringToSetAuthorDto(Set<String> authors) {
        return authors.stream()
                .map(AuthorDto::new)
                .collect(Collectors.toSet());
    }

    @Mapping(target = "bookId", source = "book.id")
    BookDto toBookDto(Book book);

    @Mapping(target = "id", source = "bookDto.bookId")
    Book toBook(BookDto bookDto);

    @Mapping(target = "isbn", expression = "java(bookItem.volumeInfo().getIsbn13())")
    @Mapping(target = "title", source = "bookItem.volumeInfo.title")
    @Mapping(target = "pages", source = "bookItem.volumeInfo.pageCount")
    @Mapping(target = "publisher", source = "bookItem.volumeInfo.publisher")
    @Mapping(target = "publishedDate", expression = "java(bookItem.volumeInfo().getPublishedDateAsLocalDate())")
    @Mapping(target = "language", source = "bookItem.volumeInfo.language")
    @Mapping(target = "description", source = "bookItem.volumeInfo.description")
    @Mapping(target = "imageLink", source = "bookItem.volumeInfo.imageLinks.thumbnail")
    @Mapping(target = "genre", expression = "java(getGenre(bookItem.volumeInfo().categories()))")
    @Mapping(target = "id", ignore = true)
    Book toBook(GoogleBooksResponse.BookItem bookItem);

}
