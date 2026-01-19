package ru.yanin.practice.user_service.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.yanin.practice.user_service.model.entity.ReadingProfile;

import java.util.Optional;

@Repository
public interface ReadingProfileRepository extends CrudRepository<ReadingProfile, Long> {

    Optional<ReadingProfile> findByUserId(Long userId);
}
