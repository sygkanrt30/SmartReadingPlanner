CREATE TABLE authors (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(200) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_name (full_name),
);

CREATE TABLE books (
    id BIGSERIAL PRIMARY KEY,
    isbn VARCHAR(20) UNIQUE NOT NULL,
    title VARCHAR(500) NOT NULL,
    pages INT NOT NULL,
    publisher VARCHAR(200),
    published_date DATE,
    language VARCHAR(50),
    genre VARCHAR(50) NOT NULL,
    description TEXT,
    average_rating DECIMAL(3,2) DEFAULT 0.00,
    image_link VARCHAR UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_isbn (isbn),
    INDEX idx_title (title(100)),
    INDEX idx_author (title(50)),
    INDEX idx_publisher (publisher),
    INDEX idx_language (language),
    INDEX idx_isbn_title (isbn, title(50)),
    CONSTRAINT chk_average_rating
        CHECK (average_rating >= 0 AND average_rating <= 5),
    CONSTRAINT chk_pages
        CHECK (pages > 0)
);

CREATE TABLE book_authors (
    book_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (book_id, author_id),
    INDEX idx_author_id (author_id),
    CONSTRAINT fk_book_authors_book
        FOREIGN KEY (book_id)
        REFERENCES books(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_book_authors_author
        FOREIGN KEY (author_id)
        REFERENCES authors(id)
        ON DELETE CASCADE
);

CREATE TABLE book_ratings (
    id BIGSERIAL PRIMARY KEY,
    book_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    rating TINYINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_book_user (book_id, user_id),
    CONSTRAINT fk_book_ratings_book
        FOREIGN KEY (book_id)
        REFERENCES books(id)
        ON DELETE CASCADE,
    CONSTRAINT chk_rating_value
        CHECK (rating >= 1 AND rating <= 5)
);

CREATE TABLE users_books (
    user_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, book_id)
);