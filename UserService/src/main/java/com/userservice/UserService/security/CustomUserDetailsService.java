package com.userservice.UserService.security;

import com.userservice.UserService.Entity.UserServerEntity;
import com.userservice.UserService.Repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo repo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserServerEntity user = repo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!user.isActive()) {
            throw new UsernameNotFoundException("User is deleted");
        }

        // ✅ RETURN CUSTOM USER DETAILS
        return new CustomUserDetails(user);
    }
}