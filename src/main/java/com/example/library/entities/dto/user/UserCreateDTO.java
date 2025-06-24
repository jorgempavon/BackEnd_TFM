package com.example.library.entities.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserCreateDTO {
    @NotBlank(message = "El dni es obligatorio")
    @Size(min = 9, max = 9, message = "El dni debe tener 9 caracteres")
    private String dni;
    @NotBlank(message = "El email es obligatorio")
    private String email;
    @NotBlank(message = "El nombre es obligatorio")
    private String name;
    private String lastName;


    public UserCreateDTO(){}

    public UserCreateDTO(String dni, String email, String name,
                         String lastName){
        this.dni = dni;
        this.email = email;
        this.name = name;
        this.lastName = lastName;
    }
}
