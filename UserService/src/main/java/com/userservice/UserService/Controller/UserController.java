package com.userservice.UserService.Controller;

import com.userservice.UserService.Entity.UserServerEntity;
import com.userservice.UserService.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService service;

    @PostMapping("/register")
    public UserServerEntity register(@RequestBody UserServerEntity user) {
        return service.register(user);
    }
}
