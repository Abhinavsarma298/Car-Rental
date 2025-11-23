package com.ownerservice.OwnerService.Repository;

import com.ownerservice.OwnerService.Entity.OwnerServerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OwnerRepo extends JpaRepository<OwnerServerEntity, Integer> {

//    boolean existsByName(String name);
//    boolean existsByEmail(String email);

    Optional<OwnerServerEntity> findByEmail(String email);
}
