package ru.yanin.practise.bookservice.service.author;

import ru.yanin.practise.bookservice.model.entity.Author;

import java.util.Optional;

public interface AuthorService {

    Optional<Author> findByFullName(String fullName);

    Author save(String fullName);
}
