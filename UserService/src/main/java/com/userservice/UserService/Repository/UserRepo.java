package com.userservice.UserService.Repository;

import com.userservice.UserService.Entity.UserServerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<UserServerEntity, Integer> {

//    boolean existsByName(String name);
//    boolean existsByEmail(String email);

    Optional<UserServerEntity> findByEmail(String email);
}
