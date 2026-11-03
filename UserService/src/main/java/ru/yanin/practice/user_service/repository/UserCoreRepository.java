package ru.yanin.practice.user_service.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.yanin.practice.user_service.model.entity.UserCore;

@Repository
public interface UserCoreRepository extends CrudRepository<UserCore, Long> {

    @Modifying
    @Query("UPDATE user_core SET first_name = :firstName, last_name = :lastName WHERE user_id = :userId")
    void updateFirstAndLastNameByUserId(Long userId, String firstName, String lastName);
}
