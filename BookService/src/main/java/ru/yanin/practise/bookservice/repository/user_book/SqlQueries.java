package ru.yanin.practise.bookservice.repository.user_book;

import lombok.experimental.UtilityClass;
import ru.yanin.practise.bookservice.repository.user_book.util.QueryReader;

@UtilityClass
class SqlQueries {
    private static final QueryReader QUERY_READER = new QueryReader("user_book_queries.sql");

    String selectUserBooksSortQuerySample() {
        return QUERY_READER.get("SELECT_BY_USER_ID_WITH_SORT_QUERY_SAMPLE");
    }

    String selectUserBooksWithPaginationQuery() {
        return QUERY_READER.get("SELECT_BY_USER_ID_WITH_PAGINATION_QUERY");
    }

    String deleteBookFromUserQuery(){
        return QUERY_READER.get("DELETE_BOOK_FROM_USER_QUERY");
    }

    String countByUserAndBookIdsQuery(){
        return QUERY_READER.get("COUNT_BY_USER_AND_BOOK_IDS_QUERY");
    }
}
