package ru.yanin.practise.bookservice.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;

public record GoogleBooksResponse(
        List<BookItem> items,
        int totalItems
) {

    public record BookItem(String id, VolumeInfo volumeInfo) {

        public record VolumeInfo(
                String title,
                Set<String> authors,
                String publisher,
                String publishedDate,
                String description,
                List<IndustryIdentifier> industryIdentifiers,
                Integer pageCount,
                List<String> categories,
                String language,
                @JsonProperty("mainCategory") String mainCategory,
                ImageLinks imageLinks,
                @JsonProperty("infoLink") String infoLink
        ) {

            public LocalDate getPublishedDateAsLocalDate() {
                if (publishedDate == null || publishedDate.trim().isEmpty()) {
                    return null;
                }

                var formats = List.of(
                        "yyyy-MM-dd",
                        "yyyy-MM",
                        "yyyy"
                );

                for (String format : formats) {
                    try {
                        if (format.length() == publishedDate.length()) {
                            var formatter = DateTimeFormatter.ofPattern(format);
                            return LocalDate.parse(publishedDate, formatter);
                        }
                    } catch (DateTimeParseException _) {
                    }
                }
                return null;
            }

            public String getIsbn10() {
                return getIndustryIdentifier("ISBN_10");
            }

            public String getIsbn13() {
                return getIndustryIdentifier("ISBN_13");
            }

            public String getPreferredIsbn() {
                String isbn13 = getIsbn13();
                return isbn13 != null ? isbn13 : getIsbn10();
            }

            private String getIndustryIdentifier(String type) {
                if (industryIdentifiers == null) return null;

                return industryIdentifiers.stream()
                        .filter(id -> type.equals(id.type()))
                        .map(IndustryIdentifier::identifier)
                        .findFirst()
                        .orElse(null);
            }
        }

        public record IndustryIdentifier(String type, String identifier) {
        }

        public record ImageLinks(@JsonProperty("thumbnail") String thumbnail) {
        }
    }
}
