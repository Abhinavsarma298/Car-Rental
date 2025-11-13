package com.userservice.UserService.Service;

import com.userservice.UserService.Entity.UserServerEntity;
import com.userservice.UserService.Repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepo repo;

    public UserServerEntity register(UserServerEntity user) {
        return repo.save(user);
    }
}
