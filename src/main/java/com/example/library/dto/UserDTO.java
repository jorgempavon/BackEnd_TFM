package com.example.library.dto;

import jakarta.validation.constraints.NotBlank;

public class UserDTO {
    private Long id;
    @NotBlank(message = "El dni es obligatorio")
    private String dni;
    @NotBlank(message = "El email es obligatorio")
    private String email;
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
    @NotBlank(message = "El nombre es obligatorio")
    private String name;
    private String lastName;

    public String getId() {
        return id;
    }
    public void setId(String dni) {
        this.dni = dni;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
