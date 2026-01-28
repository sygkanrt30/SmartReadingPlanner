package ru.yanin.practise.bookservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

@Configuration
public class DbBeans {

    @Bean
    public SimpleJdbcInsert userBookJdbcInsert(
            JdbcTemplate jdbcTemplate,
            @Value("${spring.datasource.simple-insert.user-book.schema}") String schemaName,
            @Value("${spring.datasource.simple-insert.user-book.table}") String tableName) {

        var insert = new SimpleJdbcInsert(jdbcTemplate)
                .withSchemaName(schemaName)
                .withTableName(tableName)
                .usingColumns("user_id", "book_id")
                .usingGeneratedKeyColumns("created_at");
        insert.compile();
        return insert;
    }
}
