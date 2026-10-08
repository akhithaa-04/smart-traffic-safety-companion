package com.stsc.backend.dto;

import com.stsc.backend.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    private User.VehicleType vehicleType; // optional at signup, defaults to BOTH
}
