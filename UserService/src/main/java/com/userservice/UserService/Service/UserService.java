package com.userservice.UserService.Service;

import com.userservice.UserService.Entity.UserServerEntity;
import com.userservice.UserService.Repository.UserRepo;
import com.userservice.UserService.Exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepo repo;

    // REGISTER
    public UserServerEntity register(UserServerEntity user) {

        if (repo.findByEmail(user.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException(
                    "User already exists with email: " + user.getEmail());
        }

        return repo.save(user);
    }

    // LOGIN
    public void login(UserServerEntity user) {

        UserServerEntity existingUser = repo.findByEmail(user.getEmail())
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        if (!existingUser.getPassword().equals(user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }
    }

    // UPDATE PASSWORD
    public void updatePassword(UserServerEntity user) {

        UserServerEntity existingUser = repo.findByEmail(user.getEmail())
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        if (existingUser.getPassword().equals(user.getPassword())) {
            throw new SamePasswordException(
                    "New password cannot be same as old password");
        }

        existingUser.setPassword(user.getPassword());
        repo.save(existingUser);
    }

    // UPDATE NAME
    public void updateName(String email, String name) {

        UserServerEntity user = repo.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        if (user.getName().equals(name)) {
            throw new SameNameException(
                    "New name cannot be same as old name");
        }

        user.setName(name);
        repo.save(user);
    }

    // GET BY EMAIL
    public UserServerEntity getUserByEmail(String email) {

        return repo.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));
    }

    // GET BY ID
    public UserServerEntity getUserById(int id) {

        return repo.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));
    }

    // DELETE USER
    public void deleteUser(String email) {

        UserServerEntity user = repo.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        repo.delete(user);
    }
}
