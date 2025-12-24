package com.ownerservice.OwnerService.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateNameRequest {
    @Email
    private String email;

    @NotBlank
    private String newName;
}
