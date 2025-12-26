package com.ownerservice.OwnerService.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OwnerLoginRequest {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
