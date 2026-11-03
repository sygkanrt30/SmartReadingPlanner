package ru.yanin.practice.user_service.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.yanin.practice.user_service.model.entity.UserCredentials;

import java.util.Optional;

@Repository
public interface UserCredentialsRepository extends CrudRepository<UserCredentials, Long> {

    Optional<UserCredentials> findByUsername(String username);

    Optional<UserCredentials> findByUserId(Long userId);

    @Modifying
    @Query("UPDATE users_credentials SET email_verified = true WHERE email = :email")
    void updateEmailVerificationStatus(String email);
}
