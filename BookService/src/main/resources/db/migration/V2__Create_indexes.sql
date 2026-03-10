CREATE INDEX idx_users_books_user_isbn ON users_books(user_id, isbn);

CREATE INDEX idx_books_title ON books(title);
CREATE INDEX idx_books_pages ON books(pages);
CREATE INDEX idx_books_published_date ON books(published_date);
CREATE INDEX idx_books_average_rating ON books(average_rating);