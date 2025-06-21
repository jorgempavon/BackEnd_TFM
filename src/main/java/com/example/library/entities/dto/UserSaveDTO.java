package com.example.library.entities.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserSaveDTO {
    @NotBlank(message = "El dni es obligatorio")
    private String dni;
    @NotBlank(message = "El email es obligatorio")
    private String email;
    @NotBlank(message = "El nombre es obligatorio")
    private String name;
    private String lastName;
    @NotBlank(message = "La password es obligatoria")
    private String passwordEncoded;
    @NotNull(message = "El rol es obligatorio")
    private String rol;

    public UserSaveDTO(){}

    public UserSaveDTO(String dni,String email,String name,String lastName,
                       String passwordEncoded,String rol){
        this.dni = dni;
        this.email = email;
        this.name = name;
        this.lastName = lastName;
        this.passwordEncoded = passwordEncoded;
        this.rol = rol;
    }

}
