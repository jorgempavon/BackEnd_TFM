package com.example.library.entities.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterDTO {
    @NotNull(message = "El dni es obligatorio")
    @NotBlank(message = "El dni es obligatorio")
    private String dni;
    @NotNull(message = "El email es obligatorio")
    @NotBlank(message = "El email es obligatorio")
    private String email;
    @NotNull(message = "El contraseña es obligatoria")
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
    @NotNull(message = "El confirmación de contraseña es obligatoria")
    @NotBlank(message = "La confirmación de contraseña es obligatoria")
    private String repeatPassword;
    @NotNull(message = "El nombre es obligatorio")
    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    private String lastName;
}
