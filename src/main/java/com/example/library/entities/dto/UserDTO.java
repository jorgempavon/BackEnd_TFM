package com.example.library.entities.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDTO {
    private Long id;
    @NotNull(message = "El dni es obligatorio")
    @NotBlank(message = "El dni es obligatorio")
    private String dni;
    @NotNull(message = "El email es obligatorio")
    @NotBlank(message = "El email es obligatorio")
    private String email;
    @NotBlank(message = "El nombre es obligatorio")
    private String name;
    private String lastName;
}
