COUNT_BY_USER_AND_BOOK_IDS_QUERY = SELECT COUNT(*) FROM users_books WHERE user_id = :userId AND book_id = :bookId;

DELETE_BOOK_FROM_USER_QUERY = DELETE FROM users_books WHERE user_id = :userId AND book_id IN :bookIds;

SELECT_BY_USER_ID_WITH_PAGINATION_QUERY = SELECT isbn FROM users_books WHERE user_id = :userId ORDER BY book_id LIMIT :limit OFFSET :offset;

SELECT_BY_USER_ID_WITH_SORT_QUERY_SAMPLE = SELECT b.id, b.isbn, b.title, b.pages, b.publisher, b.published_date, b.language, b.genre, b.description, b.average_rating, b.image_link, b.created_at FROM users_books ub INNER JOIN books b ON ub.book_id = b.id WHERE ub.user_id = :userId ORDER BY %s %s LIMIT :limit OFFSET :offset;