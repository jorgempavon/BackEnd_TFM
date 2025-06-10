package com.example.library.entities.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterDTO {
    @NotBlank(message = "El dni es obligatorio")
    @Size(min = 9, max = 9, message = "El dni debe tener 9 caracteres")
    private String dni;
    @NotBlank(message = "El email es obligatorio")
    private String email;
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
    @NotBlank(message = "La confirmación de contraseña es obligatoria")
    private String repeatPassword;
    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    private String lastName;


    public UserRegisterDTO (){}

    public UserRegisterDTO (String dni,String email,String password,
                            String repeatPassword,String name,String lastName){
        this.dni = dni;
        this.email = email;
        this.password = password;
        this.repeatPassword = repeatPassword;
        this.name = name;
        this.lastName = lastName;
    }

}
