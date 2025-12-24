package com.userservice.UserService.Controller;

import com.userservice.UserService.Entity.UserServerEntity;
import com.userservice.UserService.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService service;

    @PostMapping("/register")
    public ResponseEntity<UserServerEntity> register(
            @RequestBody UserServerEntity user) {

        return ResponseEntity.ok(service.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestBody UserServerEntity user) {

        service.login(user);
        return ResponseEntity.ok("Login successful");
    }

    @PutMapping("/password")
    public ResponseEntity<String> updatePassword(
            @RequestBody UserServerEntity user) {

        service.updatePassword(user);
        return ResponseEntity.ok("Password updated successfully");
    }

    @PutMapping("/name/{email}/{name}")
    public ResponseEntity<String> updateName(
            @PathVariable String email,
            @PathVariable String name) {

        service.updateName(email, name);
        return ResponseEntity.ok("Name updated successfully");
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserServerEntity> getByEmail(@PathVariable String email) {
        return ResponseEntity.ok(service.getUserByEmail(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserServerEntity> getById(@PathVariable int id) {
        return ResponseEntity.ok(service.getUserById(id));
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<String> delete(@PathVariable String email) {
        service.deleteUser(email);
        return ResponseEntity.ok("User deleted successfully");
    }
}
