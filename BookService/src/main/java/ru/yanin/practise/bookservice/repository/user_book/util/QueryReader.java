package ru.yanin.practise.bookservice.repository.user_book.util;

import ru.yanin.practise.bookservice.exception.FailedLoadFileException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public class QueryReader {
    private static final String BASE_PATH = "/db/queries/";
    private final Map<String, String> queries;

    public QueryReader(String fileName) {
        String fullPath = BASE_PATH + fileName;
        if (!fullPath.endsWith(".sql")) {
            fullPath = fullPath + ".sql";
        }
        this.queries = loadQueries(fullPath);
    }

    private Map<String, String> loadQueries(String fullPath) {
        var properties = new Properties();
        try (var inputStream = getClass().getResourceAsStream(fullPath);
             var reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            properties.load(reader);
        } catch (IOException e) {
            throw new FailedLoadFileException("Failed to load queries from: " + fullPath, e);
        }
        return fillQueryMap(properties);
    }

    private Map<String, String> fillQueryMap(Properties properties) {
        var queryMap = new HashMap<String, String>();
        for (Object key : properties.keySet()) {
            var keyStr = String.valueOf(key);
            queryMap.put(keyStr, properties.getProperty(keyStr));
        }
        return queryMap;
    }

    public String get(String queryName) {
        String query = queries.get(queryName);
        if (Objects.isNull(query)) {
            throw new IllegalArgumentException("Query not found: " + queryName);
        }
        return query;
    }
}
