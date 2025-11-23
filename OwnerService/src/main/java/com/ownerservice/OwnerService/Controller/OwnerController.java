package com.ownerservice.OwnerService.Controller;

import com.ownerservice.OwnerService.Entity.OwnerServerEntity;
import com.ownerservice.OwnerService.Service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/owners")
public class OwnerController {

    @Autowired
    private OwnerService service;

    @PostMapping("/register")
    public OwnerServerEntity register(@RequestBody OwnerServerEntity owner) {
        return service.register(owner);
    }

    @GetMapping("/getOwner/{email}")
    public OwnerServerEntity getUserByEmail(@PathVariable String email) {
        return service.getOwnerByEmail(email);
    }

    @PostMapping("/login")
    public String login(@RequestBody OwnerServerEntity owner) {
        return service.login(owner);
    }

    @PutMapping("/update-password")
    public String updatePassword(@RequestBody OwnerServerEntity owner) {
        return service.updatePassword(owner);
    }

    @PutMapping("/update-username/{email}/{name}")
    public String updateName(@PathVariable String email, @PathVariable String name) {
        return service.updateName(email, name);
    }

    @DeleteMapping("/delete-owner/{email}")
    public String deleteOwner(@PathVariable String email) {
        return service.deleteOwner(email);
    }

    @GetMapping("/getOwner/{id}")
    public OwnerServerEntity getOwnerById(@PathVariable int id) {
        return service.getOwnerById(id);
    }
}
