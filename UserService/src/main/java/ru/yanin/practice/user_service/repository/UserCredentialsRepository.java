package ru.yanin.practice.user_service.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.yanin.practice.user_service.model.entity.UserCredentials;

import java.util.UUID;

@Repository
public interface UserCredentialsRepository extends CrudRepository<UserCredentials, Long> {

    @Modifying
    @Query("UPDATE UserCredentials uc SET uc.current_token_id = :tokenId WHERE uc.user_id = :userId")
    void addTokenIdToUser(UUID tokenId, Long userId);
}
