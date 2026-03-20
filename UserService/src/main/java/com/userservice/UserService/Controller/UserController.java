package com.userservice.UserService.Controller;

import com.userservice.UserService.Entity.UserServerEntity;
import com.userservice.UserService.Service.UserService;
import com.userservice.UserService.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private JwtUtil jwtUtil;

    // ✅ REGISTER (OPEN)
    @PostMapping("/register")
    public ResponseEntity<UserServerEntity> register(
            @RequestBody UserServerEntity user) {

        return ResponseEntity.ok(service.register(user));
    }

    // ✅ LOGIN (OPEN)
    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestBody UserServerEntity user) {

        UserServerEntity loggedUser = service.login(user);

        String token = jwtUtil.generateToken(loggedUser.getEmail());

        return ResponseEntity.ok(token);
    }

    // 🟢 ALL LOGGED-IN USERS
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/password")
    public ResponseEntity<String> updatePassword(
            @RequestBody UserServerEntity user) {

        service.updatePassword(user);
        return ResponseEntity.ok("Password updated successfully");
    }

    // 🟢 ALL LOGGED-IN USERS
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/name/{email}/{name}")
    public ResponseEntity<String> updateName(
            @PathVariable String email,
            @PathVariable String name) {

        service.updateName(email, name);
        return ResponseEntity.ok("Name updated successfully");
    }

    // 🟡 USER + ADMIN
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/email/{email}")
    public ResponseEntity<UserServerEntity> getByEmail(@PathVariable String email) {
        return ResponseEntity.ok(service.getUserByEmail(email));
    }

    // 🟡 USER + ADMIN
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserServerEntity> getById(@PathVariable int id) {
        return ResponseEntity.ok(service.getUserById(id));
    }

    // 🔴 ADMIN ONLY
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{email}")
    public ResponseEntity<String> delete(@PathVariable String email) {
        service.deleteUser(email);
        return ResponseEntity.ok("User deleted successfully");
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/me")
    public ResponseEntity<String> deleteMyAccount() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        service.deleteUser(email);

        return ResponseEntity.ok("Your account deleted successfully");
    }
}