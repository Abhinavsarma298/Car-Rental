package com.userservice.UserService.Service;

import com.userservice.UserService.Entity.UserServerEntity;
import com.userservice.UserService.Exceptions.*;
import com.userservice.UserService.Repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.Name;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepo repo;

    public UserServerEntity register(UserServerEntity user) {
        Optional<UserServerEntity> existingUser = repo.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException("User Already Exists with this E-Mail - " + user.getEmail() + ". Please try with another E-Mail ID"); // Custom Exceptions - UserAlreadyExistsException
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
                throw new InvalidCredentialsException("Either E-Mail or Password are Incorrect."); // Custom Exceptions - InvalidCredentialsException
            }
        } else {
            throw new UserNotFoundException("Either E-Mail or Password are Incorrect."); // Custom Exceptions - UserNotFoundExceptio
        }
    }

    public String updatePassword(UserServerEntity user) {
        Optional<UserServerEntity> existingUser = repo.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            UserServerEntity foundUser = existingUser.get();
            if (foundUser.getPassword().equals(user.getPassword())) {
                throw new SamePasswordException("New password cannot be the same as the old password"); // Custom Exceptions - SamePasswordException
            }
            foundUser.setPassword(user.getPassword());
            repo.save(foundUser);
            return "Password Updated Successfully";
        } else {
            throw new UserNotFoundException("User Not found with E-Mail - "+user.getEmail()); // Custom Exceptions - UserNotFoundException
        }
    }

    public String updateName(String email, String name) {
        Optional<UserServerEntity> existingUser = repo.findByEmail(email);
        if (existingUser.isPresent()) {
            UserServerEntity foundUser = existingUser.get();
            if (foundUser.getName().equals(name)) {
                throw new SameNameException("New name cannot be the same as the old name"); // Custom Exceptions - SameNameException
            }
            foundUser.setName(name);
            repo.save(foundUser);
            return "Name Updated Successfully";
        } else {
            throw new UserNotFoundException("User Not found with E-Mail - "+email); // Custom Exceptions - UserNotFoundException
        }
    }

    public String deleteUser(String email) {
        Optional<UserServerEntity> existingUser = repo.findByEmail(email);
        if (existingUser.isPresent()) {
            repo.delete(existingUser.get());
            return "User Deleted Successfully";
        } else {
            throw new UserNotFoundException("User Not found with E-Mail - "+email); // Custom Exceptions - UserNotFoundException
        }
    }

    public UserServerEntity getUserByEmail(String email) {
        Optional<UserServerEntity> existingUser = repo.findByEmail(email);
        if (existingUser.isPresent()) {
            return existingUser.get();
        } else {
            throw new UserNotFoundException("User Not found with E-Mail - "+email); // Custom Exceptions - UserAlreadyExistsException
        }
    }

    public UserServerEntity getUserById(int id) {
        Optional<UserServerEntity> existingUser = repo.findById(id);
        if (existingUser.isPresent()) {
            return existingUser.get();
        } else {
            throw new UserNotFoundException("User Not found with ID - "+id); // Custom Exceptions - UserAlreadyExistsException
        }
    }
}