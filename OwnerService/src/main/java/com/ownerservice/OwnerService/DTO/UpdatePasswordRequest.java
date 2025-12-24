package com.ownerservice.OwnerService.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePasswordRequest {
    @Email
    private String email;

    @NotBlank
    private String newPassword;
}
