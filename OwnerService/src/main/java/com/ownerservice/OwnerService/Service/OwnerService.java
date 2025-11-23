package com.ownerservice.OwnerService.Service;

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

    public OwnerServerEntity register(OwnerServerEntity owner) {
        Optional<OwnerServerEntity> existingOwner = repo.findByEmail(owner.getEmail());
        if (existingOwner.isPresent()) {
            throw new OwnerAlreadyExistsException("Owner Already Exists with this E-Mail - " + owner.getEmail() + ". Please try with another E-Mail ID");
        }
        return repo.save(owner);
    }

    public String login(OwnerServerEntity owner) {
        Optional<OwnerServerEntity> existingOwner = repo.findByEmail(owner.getEmail());
        if (existingOwner.isPresent()) {
            OwnerServerEntity foundOwner = existingOwner.get();
            if (foundOwner.getPassword().equals(owner.getPassword())) {
                return "Login Successful";
            } else {
                throw new InvalidCredentialsException("Either E-Mail or Password are Incorrect.");
            }
        } else {
            throw new OwnerNotFoundException("Either E-Mail or Password are Incorrect.");
        }
    }

    public String updatePassword(OwnerServerEntity owner) {
        Optional<OwnerServerEntity> existingOwner = repo.findByEmail(owner.getEmail());
        if (existingOwner.isPresent()) {
            OwnerServerEntity foundOwner = existingOwner.get();
            if (foundOwner.getPassword().equals(owner.getPassword())) {
                throw new SamePasswordException("New password cannot be the same as the old password");
            }
            foundOwner.setPassword(owner.getPassword());
            repo.save(foundOwner);
            return "Password Updated Successfully";
        } else {
            throw new OwnerNotFoundException("Owner Not found with E-Mail - " + owner.getEmail());
        }
    }

    public String updateName(String email, String name) {
        Optional<OwnerServerEntity> existingOwner = repo.findByEmail(email);
        if (existingOwner.isPresent()) {
            OwnerServerEntity foundOwner = existingOwner.get();
            if (foundOwner.getName().equals(name)) {
                throw new SameNameException("New name cannot be the same as the old name");
            }
            foundOwner.setName(name);
            repo.save(foundOwner);
            return "Name Updated Successfully";
        } else {
            throw new OwnerNotFoundException("Owner Not found with E-Mail - " + email);
        }
    }

    public String deleteOwner(String email) {
        Optional<OwnerServerEntity> existingOwner = repo.findByEmail(email);
        if (existingOwner.isPresent()) {
            repo.delete(existingOwner.get());
            return "Owner Deleted Successfully";
        } else {
            throw new OwnerNotFoundException("Owner Not found with E-Mail - " + email);
        }
    }

    public OwnerServerEntity getOwnerByEmail(String email) {
        Optional<OwnerServerEntity> existingOwner = repo.findByEmail(email);
        if (existingOwner.isPresent()) {
            return existingOwner.get();
        } else {
            throw new OwnerNotFoundException("Owner Not found with E-Mail - " + email);
        }
    }

    public OwnerServerEntity getOwnerById(int id) {
        Optional<OwnerServerEntity> existingOwner = repo.findById(id);
        if (existingOwner.isPresent()) {
            return existingOwner.get();
        } else {
            throw new OwnerNotFoundException("Owner Not found with ID - " + id);
        }
    }
}
