package com.carservice.CarService.Controller;

import com.carservice.CarService.Entity.CarServiceEntity;
import com.carservice.CarService.Service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cars")
public class CarController {

    @Autowired
    private CarService service;

    // Register a new car
    @PostMapping("/register")
    public CarServiceEntity register(@RequestBody CarServiceEntity car) {
        return service.register(car);
    }

    // Get ALL cars by owner id
    @GetMapping("/owner/{ownerId}")
    public List<CarServiceEntity> getCarsByOwnerId(@PathVariable Integer ownerId) {
        return service.getCarsByOwnerId(ownerId);
    }

    // Get ALL cars by brand (vehicleName)
    @GetMapping("/brand/{brand}")
    public List<CarServiceEntity> getCarsByBrand(@PathVariable String brand) {
        return service.getCarsByBrand(brand);
    }

    // Get car by id
    @GetMapping("/id/{id}")
    public CarServiceEntity getCarById(@PathVariable Integer id) {
        return service.getCarById(id);
    }

    @DeleteMapping("/delete/{id}")
    public String removeCarById(@PathVariable Integer id) {

        return service.removeCarById(id);

    }

}
