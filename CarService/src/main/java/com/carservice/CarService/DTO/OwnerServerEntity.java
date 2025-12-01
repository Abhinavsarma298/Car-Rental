package com.carservice.CarService.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OwnerServerEntity {

    private Integer id;
    private String name;
    private String email;
    private String phone;
    private String address;

}
