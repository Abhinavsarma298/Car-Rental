package com.userservice.UserService.Service;

import com.userservice.UserService.Entity.UserServerEntity;
import com.userservice.UserService.Repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.Name;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepo repo;

    public UserServerEntity register(UserServerEntity user) {
        Optional<UserServerEntity> existingUser = repo.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            return null; // Custom Exceptions - UserAlreadyExistsException
        }
        return repo.save(user);
    }

    public String login(UserServerEntity user) {
        Optional<UserServerEntity> existingUser = repo.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            UserServerEntity foundUser = existingUser.get();
            if (foundUser.getPassword().equals(user.getPassword())) {
                return "Login Successful";
            } else {
                return "Invalid Password"; // Custom Exceptions - InvalidCredentialsException
            }
        } else {
            return "User Not Found"; // Custom Exceptions - UserNotFoundException
        }
    }

    public String updatePassword(UserServerEntity user) {
        Optional<UserServerEntity> existingUser = repo.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            UserServerEntity foundUser = existingUser.get();
            if (foundUser.getPassword().equals(user.getPassword())) {
                return "New password cannot be the same as the old password"; // Custom Exceptions - SamePasswordException
            }
            foundUser.setPassword(user.getPassword());
            repo.save(foundUser);
            return "Password Updated Successfully";
        } else {
            return "User Not Found"; // Custom Exceptions - UserNotFoundException
        }
    }

    public String updateName(String email, String name) {
        Optional<UserServerEntity> existingUser = repo.findByEmail(email);
        if (existingUser.isPresent()) {
            UserServerEntity foundUser = existingUser.get();
            if (foundUser.getName().equals(name)) {
                return "New name cannot be the same as the old name"; // Custom Exceptions - SameNameException
            }
            foundUser.setName(name);
            repo.save(foundUser);
            return "Name Updated Successfully";
        } else {
            return "User Not Found"; // Custom Exceptions - UserNotFoundException
        }
    }

    public String deleteUser(String email) {
        Optional<UserServerEntity> existingUser = repo.findByEmail(email);
        if (existingUser.isPresent()) {
            repo.delete(existingUser.get());
            return "User Deleted Successfully";
        } else {
            return "User Not Found"; // Custom Exceptions - UserNotFoundException
        }
    }
}
