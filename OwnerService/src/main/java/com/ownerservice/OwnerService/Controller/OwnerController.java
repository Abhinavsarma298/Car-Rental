package com.ownerservice.OwnerService.Controller;

import com.ownerservice.OwnerService.DTO.OwnerLoginRequest;
import com.ownerservice.OwnerService.DTO.OwnerRegisterRequest;
import com.ownerservice.OwnerService.DTO.UpdateNameRequest;
import com.ownerservice.OwnerService.DTO.UpdatePasswordRequest;
import com.ownerservice.OwnerService.Entity.OwnerServerEntity;
import com.ownerservice.OwnerService.Service.OwnerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/owners")
public class OwnerController {

    @Autowired
    private OwnerService service;

    @PostMapping("/register")
    public ResponseEntity<OwnerServerEntity> register(
            @Valid @RequestBody OwnerRegisterRequest request) {

        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(
            @Valid @RequestBody OwnerLoginRequest request) {

        service.login(request);
        return ResponseEntity.ok("Login successful");
    }

    @PutMapping("/password")
    public ResponseEntity<String> updatePassword(
            @Valid @RequestBody UpdatePasswordRequest request) {

        service.updatePassword(request);
        return ResponseEntity.ok("Password updated successfully");
    }

    @PutMapping("/name")
    public ResponseEntity<String> updateName(
            @Valid @RequestBody UpdateNameRequest request) {

        service.updateName(request);
        return ResponseEntity.ok("Name updated successfully");
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<OwnerServerEntity> getByEmail(@PathVariable String email) {
        return ResponseEntity.ok(service.getByEmail(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OwnerServerEntity> getById(@PathVariable int id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<String> delete(@PathVariable String email) {
        service.delete(email);
        return ResponseEntity.ok("Owner deleted successfully");
    }
}
