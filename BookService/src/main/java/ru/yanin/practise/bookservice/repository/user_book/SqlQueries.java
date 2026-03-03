package ru.yanin.practise.bookservice.repository.user_book;

import lombok.experimental.UtilityClass;
import ru.yanin.practise.bookservice.repository.user_book.util.QueryReader;

@UtilityClass
class SqlQueries {
    private static final QueryReader QUERY_READER = new QueryReader("/db/queries/user_book_queries.yaml");

    String selectUserBooksSortQuerySample() {
        return QUERY_READER.get("SELECT_BY_USER_ID_WITH_SORT_QUERY_SAMPLE");
    }

    String deleteBookFromUserQuery(){
        return QUERY_READER.get("DELETE_BOOK_FROM_USER_QUERY");
    }

    String countByUserAndBookIdsQuery(){
        return QUERY_READER.get("COUNT_BY_USER_AND_BOOK_IDS_QUERY");
    }

    String selectByUserIdAndGenresQuerySample(){
        return QUERY_READER.get("SELECT_BY_USER_ID_AND_FAVORITE_GENRES_QUERY_SAMPLE");
    }
}
