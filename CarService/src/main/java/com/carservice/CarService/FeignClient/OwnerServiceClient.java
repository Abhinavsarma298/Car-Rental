package com.carservice.CarService.FeignClient;

import com.carservice.CarService.DTO.OwnerServerEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "OWNER-SERVICE")
public interface OwnerServiceClient {

    @GetMapping("/owners/getOwner/id/{id}")
    public OwnerServerEntity getOwnerById(@PathVariable int id);




}
