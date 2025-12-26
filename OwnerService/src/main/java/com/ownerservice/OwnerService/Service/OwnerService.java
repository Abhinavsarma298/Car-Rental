package com.ownerservice.OwnerService.Service;

import com.ownerservice.OwnerService.DTO.OwnerLoginRequest;
import com.ownerservice.OwnerService.DTO.OwnerRegisterRequest;
import com.ownerservice.OwnerService.DTO.UpdateNameRequest;
import com.ownerservice.OwnerService.DTO.UpdatePasswordRequest;
import com.ownerservice.OwnerService.Entity.OwnerServerEntity;
import com.ownerservice.OwnerService.Exception.*;
import com.ownerservice.OwnerService.Repository.OwnerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class OwnerService {

    @Autowired
    private OwnerRepo repo;

    public OwnerServerEntity register(OwnerRegisterRequest request) {

        if (repo.findByEmail(request.getEmail()).isPresent()) {
            throw new OwnerAlreadyExistsException(
                    "Owner already exists with email: " + request.getEmail()
            );
        }

        OwnerServerEntity owner = new OwnerServerEntity();
        owner.setName(request.getName());
        owner.setEmail(request.getEmail());
        owner.setPassword(request.getPassword()); // hashing later
        owner.setPhone(request.getPhone());
        owner.setAddress(request.getAddress());

        return repo.save(owner);
    }

    public void login(OwnerLoginRequest request) {

        OwnerServerEntity owner = repo.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new OwnerNotFoundException("Invalid email or password"));

        if (!owner.getPassword().equals(request.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
    }

    public void updatePassword(UpdatePasswordRequest request) {

        OwnerServerEntity owner = repo.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new OwnerNotFoundException("Owner not found"));

        if (owner.getPassword().equals(request.getNewPassword())) {
            throw new SamePasswordException("New password must be different");
        }

        owner.setPassword(request.getNewPassword());
        repo.save(owner);
    }

    public void updateName(UpdateNameRequest request) {

        OwnerServerEntity owner = repo.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new OwnerNotFoundException("Owner not found"));

        if (owner.getName().equals(request.getNewName())) {
            throw new SameNameException("New name must be different");
        }

        owner.setName(request.getNewName());
        repo.save(owner);
    }

    public OwnerServerEntity getByEmail(String email) {
        return repo.findByEmail(email)
                .orElseThrow(() ->
                        new OwnerNotFoundException("Owner not found"));
    }

    public OwnerServerEntity getById(int id) {
        return repo.findById(id)
                .orElseThrow(() ->
                        new OwnerNotFoundException("Owner not found"));
    }

    public void delete(String email) {
        OwnerServerEntity owner = getByEmail(email);
        repo.delete(owner);
    }
}
