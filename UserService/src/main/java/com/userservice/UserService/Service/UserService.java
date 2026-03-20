package com.userservice.UserService.Service;

import com.userservice.UserService.Entity.Role;
import com.userservice.UserService.Entity.UserServerEntity;
import com.userservice.UserService.Repository.UserRepo;
import com.userservice.UserService.Exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    @Autowired
    private UserRepo repo;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // ✅ REGISTER
    public UserServerEntity register(UserServerEntity user) {

        if (repo.findByEmail(user.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException(
                    "User already exists with email: " + user.getEmail());
        }

        user.setRole(Role.USER);

        // 🔥 ENCRYPT PASSWORD
        user.setPassword(encoder.encode(user.getPassword()));

        return repo.save(user);
    }

    // 🔐 LOGIN
    public UserServerEntity login(UserServerEntity user) {

        UserServerEntity existingUser = repo.findByEmail(user.getEmail())
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        // ❌ BLOCK DELETED USER
        if (!existingUser.isActive()) {
            throw new AccountDeletedException("Account is deleted");
        }

        // 🔥 BCRYPT CHECK
        if (!encoder.matches(user.getPassword(), existingUser.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        return existingUser;
    }

    // 🔐 UPDATE PASSWORD
    public void updatePassword(UserServerEntity user) {

        UserServerEntity existingUser = repo.findByEmail(user.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!existingUser.isActive()) {
            throw new RuntimeException("User is deleted");
        }

        if (encoder.matches(user.getPassword(), existingUser.getPassword())) {
            throw new SamePasswordException("New password cannot be same");
        }

        existingUser.setPassword(encoder.encode(user.getPassword()));
        repo.save(existingUser);
    }

    // 🔐 UPDATE NAME
    public void updateName(String email, String name) {

        UserServerEntity user = repo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!user.isActive()) {
            throw new RuntimeException("User is deleted");
        }

        if (user.getName().equals(name)) {
            throw new SameNameException("New name cannot be same");
        }

        user.setName(name);
        repo.save(user);
    }

    // 🔐 GET BY EMAIL
    public UserServerEntity getUserByEmail(String email) {

        UserServerEntity user = repo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!user.isActive()) {
            throw new RuntimeException("User is deleted");
        }

        return user;
    }

    // 🔐 GET BY ID
    public UserServerEntity getUserById(int id) {

        UserServerEntity user = repo.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!user.isActive()) {
            throw new RuntimeException("User is deleted");
        }

        return user;
    }

    // 🔥 SOFT DELETE
    public void deleteUser(String email) {

        UserServerEntity user = repo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setActive(false);
        user.setDeletedAt(LocalDateTime.now());

        String loggedUser = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        user.setDeletedBy(loggedUser);

        repo.save(user);
    }
}