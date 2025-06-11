package com.example.library.entities.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDTO {
    private Long id;
    @NotBlank(message = "El dni es obligatorio")
    private String dni;
    @NotBlank(message = "El email es obligatorio")
    private String email;
    @NotBlank(message = "El nombre es obligatorio")
    private String name;
    private String lastName;
    private Boolean isAdmin;

    public UserDTO (){}

    public UserDTO (String name, String email,String dni,String lastName,Boolean isAdmin){
        this.name = name;
        this.email = email;
        this.dni = dni;
        this.lastName = lastName;
        this.isAdmin = isAdmin;
    }
}
