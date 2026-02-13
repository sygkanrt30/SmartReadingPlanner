package ru.yanin.practise.bookservice.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public record OpenLibrarySearchResponse(
        int numFound,
        List<OpenLibraryWork> docs
) {
    public record OpenLibraryWork(
            String key,
            String title,
            @JsonProperty("author_name") List<String> authorName,
            @JsonProperty("author_key") List<String> authorKey,
            @JsonProperty("subject_facet") List<String> genres,
            @JsonProperty("first_publish_year") Integer firstPublishYear,
            OpenLibraryEditionData editions
    ) {
    }

    public record OpenLibraryEditionData(
            @JsonProperty("docs") List<OpenLibraryEdition> editions
    ) {
    }

    public record OpenLibraryEdition(
            String key,
            String title,
            @JsonProperty("publishers") List<String> publisher,
            @JsonProperty("publish_date") String publishDate,
            @JsonProperty("number_of_pages") Integer pages,
            @JsonProperty("isbn_13") List<String> isbn13,
            @JsonProperty("isbn_10") List<String> isbn10,
            List<String> language,
            Object description,
            @JsonProperty("cover_i") Integer coverId
    ) {

        public LocalDate getPublishedDateAsLocalDate() {
            if (publishDate == null || publishDate.trim().isEmpty()) {
                return null;
            }

            var formats = List.of(
                    DateTimeFormatter.ISO_LOCAL_DATE,
                    DateTimeFormatter.ofPattern("yyyy-MM"),
                    DateTimeFormatter.ofPattern("yyyy"),
                    DateTimeFormatter.ofPattern("MMMM d, yyyy"),
                    DateTimeFormatter.ofPattern("d MMMM yyyy"),
                    DateTimeFormatter.ofPattern("yyyy/MM/dd")
            );

            for (var formatter : formats) {
                try {
                    return LocalDate.parse(publishDate, formatter);
                } catch (DateTimeParseException _) {
                }
            }
            return null;
        }

        public String getIsbn10() {
            if (isbn10 == null || isbn10.isEmpty()) return null;
            return isbn10.getFirst();
        }

        public String getIsbn13() {
            if (isbn13 == null || isbn13.isEmpty()) return null;
            return isbn13.getFirst();
        }

        public String getPreferredIsbn() {
            String isbn13 = getIsbn13();
            return isbn13 != null ? isbn13 : getIsbn10();
        }
    }
}
