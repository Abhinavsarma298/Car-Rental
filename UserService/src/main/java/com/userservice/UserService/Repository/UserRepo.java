package com.userservice.UserService.Repository;

import com.userservice.UserService.Entity.UserServerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<UserServerEntity, Integer> {

}
