package com.example.library.entities.dto;

import com.example.library.entities.dto.validator.AtLeastOneField;
import com.example.library.entities.model.User;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AtLeastOneField(fields = {"dni","email","password","repeatPassword","name","lastName","isAdmin"})
public class UserUpdateDTO {
    @Size(min = 9, max = 9, message = "El dni debe tener 9 caracteres")
    private String dni;
    private String email;
    private String password;
    private String repeatPassword;
    private String name;

    private String lastName;

    private Boolean isAdmin;

}
