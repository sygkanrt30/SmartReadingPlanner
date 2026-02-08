package ru.yanin.practise.bookservice.service.author;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yanin.practise.bookservice.model.entity.Author;
import ru.yanin.practise.bookservice.model.mapper.AuthorMapper;
import ru.yanin.practise.bookservice.repository.AuthorRepository;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorMapper authorMapper;
    private final AuthorRepository authorRepository;

    @Override
    public Optional<Author> findByFullName(String fullName) {
        return authorRepository.findByFullName(fullName);
    }

    @Override
    public Author save(String fullName) {
        Author author = authorMapper.toAuthor(fullName);
        return authorRepository.save(author);
    }
}
