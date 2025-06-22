package com.example.library.entities.dto;

import com.example.library.entities.dto.validator.AtLeastOneField;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AtLeastOneField(fields = {"dni","email","resetPassword","name","lastName"})
public class UserAdminUpdateDTO {
    @Size(min = 9, max = 9, message = "El dni debe tener 9 caracteres")
    private String dni;
    private String email;
    private Boolean resetPassword;
    private String name;

    private String lastName;

    public UserAdminUpdateDTO(String dni, String email, Boolean resetPassword, String name, String lastName) {
        this.dni = dni;
        this.email = email;
        this.resetPassword = resetPassword;
        this.name = name;
        this.lastName = lastName;
    }
}
