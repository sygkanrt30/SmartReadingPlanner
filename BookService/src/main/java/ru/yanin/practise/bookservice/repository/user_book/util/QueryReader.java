package ru.yanin.practise.bookservice.repository.user_book.util;

import org.yaml.snakeyaml.Yaml;
import ru.yanin.practise.bookservice.exception.FailedLoadFileException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class QueryReader {
    private static final String QUERIES_KEY = "queries";
    private final Map<String, String> queries;

    public QueryReader(String fullPath) {
        if (!fullPath.endsWith(".yaml")) {
            fullPath = fullPath + ".yaml";
        }
        this.queries = loadQueries(fullPath);
    }

    private Map<String, String> loadQueries(String fullPath) {
        Map<String, Object> yamlData;
        try (var inputStream = getClass().getResourceAsStream(fullPath);
             var reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            yamlData = new Yaml().load(reader);
        } catch (IOException e) {
            throw new FailedLoadFileException("Failed to load queries from: " + fullPath, e);
        }
        var result = fillQueriesMap(yamlData);

        if (result.isEmpty()) {
            throw new FailedLoadFileException("No queries found in YAML file: " + fullPath);
        }
        return result;
    }

    private Map<String, String> fillQueriesMap(Map<String, Object> yamlData) {
        var result = new HashMap<String, String>();
        if (Objects.nonNull(yamlData) && yamlData.containsKey(QUERIES_KEY)) {
            Object queriesObj = yamlData.get(QUERIES_KEY);
            if (queriesObj instanceof Map<?, ?> queriesMap) {
                for (var entry : queriesMap.entrySet()) {
                    String key = entry.getKey().toString();
                    Object value = entry.getValue();
                    if (value instanceof String string) {
                        result.put(key, cleanQuery(string));
                    }
                }
            }
        }
        return result;
    }

    private String cleanQuery(String query) {
        return query.trim().replaceAll("\\s+", " ");
    }

    public String get(String queryName) {
        String query = queries.get(queryName);
        if (Objects.isNull(query)) {
            var errorMessage = String.format("Query not found: %s. Available queries: %s", queryName, queries.keySet());
            throw new IllegalArgumentException(errorMessage);
        }
        return query;
    }

    public Map<String, String> getAllQueries() {
        return new HashMap<>(queries);
    }
}
