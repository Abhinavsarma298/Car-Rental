package com.carservice.CarService.Controller;

import com.carservice.CarService.DTO.CarRegisterDto;
import com.carservice.CarService.Entity.CarServiceEntity;
import com.carservice.CarService.Service.CarService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/cars")
public class CarController {

    @Autowired
    private CarService service;



    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CarServiceEntity> register(
            @RequestPart("carData") CarRegisterDto carDto,
            @RequestPart(value = "image", required = false) MultipartFile imageFile
    ) throws IOException {

        // map DTO -> Entity
        CarServiceEntity car = new CarServiceEntity();
        BeanUtils.copyProperties(carDto, car);

        // set image bytes if present (storing full image in DB as LOB)
        if (imageFile != null && !imageFile.isEmpty()) {
            car.setImage(imageFile.getBytes());
        }

        CarServiceEntity saved = service.register(car);
        return ResponseEntity.status(201).body(saved);
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
