package ru.yanin.practise.bookservice.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;
import org.hibernate.proxy.HibernateProxy;
import ru.yanin.shared.genre.Genre;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"bookAuthors", "ratings"})
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String isbn;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(nullable = false)
    private Integer pages;

    @Column(length = 200)
    private String publisher;

    @Column(name = "published_date")
    private LocalDate publishedDate;

    @Column(length = 50)
    private String language;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Genre genre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Column(name = "image_link", unique = true)
    private String imageLink;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BookAuthor> bookAuthors = new HashSet<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BookRating> ratings = new HashSet<>();

    @Formula("(SELECT COUNT(*) FROM book_authors ba WHERE ba.book_id = id)")
    private Integer authorCount;

    @Transient
    public Set<Author> getAuthors() {
        return bookAuthors.stream()
                .map(BookAuthor::getAuthor)
                .collect(Collectors.toSet());
    }

    public void addAuthor(Author author) {
        var bookAuthor = BookAuthor.builder()
                .book(this)
                .author(author)
                .build();
        bookAuthors.add(bookAuthor);
        author.getBookAuthors().add(bookAuthor);
    }

    public void addRating(Long userId, Integer ratingValue) {
        var rating = BookRating.builder()
                .book(this)
                .userId(userId)
                .rating(ratingValue)
                .build();
        ratings.add(rating);
        updateAverageRating();
    }

    private void updateAverageRating() {
        if (!ratings.isEmpty()) {
            double avg = ratings.stream()
                    .mapToInt(BookRating::getRating)
                    .average()
                    .orElse(0.0);
            averageRating = BigDecimal.valueOf(avg)
                    .setScale(2, RoundingMode.HALF_UP);
        } else {
            averageRating = BigDecimal.ZERO;
        }
    }

    @PrePersist
    @PreUpdate
    private void validate() {
        if (pages <= 0) {
            throw new IllegalArgumentException("The number of pages must be positive");
        }
        if (averageRating.compareTo(BigDecimal.ZERO) < 0 ||
                averageRating.compareTo(BigDecimal.valueOf(5)) > 0) {
            throw new IllegalArgumentException("The rating must be from 0 to 5");
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy proxy ?
                proxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy ?
                proxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        var book = (Book) o;
        return getId() != null && Objects.equals(getId(), book.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy proxy ?
                proxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
