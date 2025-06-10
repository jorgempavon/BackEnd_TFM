package com.example.library.entities.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserCreateDTO {
    @NotBlank(message = "El dni es obligatorio")
    private String dni;
    @NotBlank(message = "El email es obligatorio")
    private String email;
    @NotBlank(message = "El nombre es obligatorio")
    private String name;
    private String lastName;
    @NotBlank(message = "El rol es obligatorio")
    private boolean isAdmin;

    public UserCreateDTO(){}

    public UserCreateDTO(String dni, String email, String name, String lastName,
                         String passwordEncoded, boolean isAdmin){
        this.dni = dni;
        this.email = email;
        this.name = name;
        this.lastName = lastName;
        this.passwordEncoded = passwordEncoded;
        this.isAdmin = isAdmin;
    }
}
