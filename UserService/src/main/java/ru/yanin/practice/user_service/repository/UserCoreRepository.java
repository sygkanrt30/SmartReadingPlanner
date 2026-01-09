package ru.yanin.practice.user_service.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.yanin.practice.user_service.model.entity.UserCore;

@Repository
public interface UserCoreRepository extends CrudRepository<UserCore, Long> {

}
