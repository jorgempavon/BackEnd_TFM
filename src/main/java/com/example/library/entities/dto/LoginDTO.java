package com.example.library.entities.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDTO {
    @NotBlank(message = "El email es obligatorio")
    private String email;
    @NotBlank(message = "La password es obligatoria")
    private String password;
}
