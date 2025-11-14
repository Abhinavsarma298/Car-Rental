package com.userservice.UserService.Controller;

import com.userservice.UserService.Entity.UserServerEntity;
import com.userservice.UserService.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService service;

    @PostMapping("/register")
    public UserServerEntity register(@RequestBody UserServerEntity user) {
        return service.register(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody UserServerEntity user) {
        return service.login(user);
    }

    @PutMapping("/update-password")
    public String updatePassword(@RequestBody UserServerEntity user) {
        return service.updatePassword(user);
    }

    @PutMapping("/update-username/{email}/{name}")
    public String updateName(@PathVariable String email, @PathVariable String name) {
        return service.updateName(email, name);
    }

    @DeleteMapping("/delete-user/{email}")
    public String deleteUser(@PathVariable String email) {
        return service.deleteUser(email);
    }
}