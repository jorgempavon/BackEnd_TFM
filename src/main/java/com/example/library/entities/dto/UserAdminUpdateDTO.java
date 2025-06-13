package com.example.library.entities.dto;

import com.example.library.entities.dto.validator.AtLeastOneField;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AtLeastOneField(fields = {"dni","email","resetPassword","name","lastName","isAdmin"})
public class UserAdminUpdateDTO {
    @Size(min = 9, max = 9, message = "El dni debe tener 9 caracteres")
    private String dni;
    private String email;
    private Boolean resetPassword;
    private String name;

    private String lastName;

    private Boolean isAdmin;
    public UserAdminUpdateDTO(String dni, String email, Boolean resetPassword, String name, String lastName, Boolean isAdmin) {
        this.dni = dni;
        this.email = email;
        this.resetPassword = resetPassword;
        this.name = name;
        this.lastName = lastName;
        this.isAdmin = isAdmin;
    }
}
