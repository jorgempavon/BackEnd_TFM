package com.example.library.entities.dto;

import com.example.library.entities.dto.validator.AtLeastOneField;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AtLeastOneField(fields = {"dni","email","password","repeatPassword","oldPassword","name","lastName"})
public class UserSelfUpdateDTO {
    @Size(min = 9, max = 9, message = "El dni debe tener 9 caracteres")
    private String dni;
    private String email;
    private String oldPassword;
    private String password;
    private String repeatPassword;
    private String name;
    private String lastName;

    public UserSelfUpdateDTO(String dni, String email, String oldPassword, String password,
                             String repeatPassword, String name, String lastName) {
        this.dni = dni;
        this.email = email;
        this.oldPassword = oldPassword;
        this.password = password;
        this.repeatPassword = repeatPassword;
        this.name = name;
        this.lastName = lastName;
    }


}
