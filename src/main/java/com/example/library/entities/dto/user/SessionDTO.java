package com.example.library.entities.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SessionDTO {
    private Long id;
    private String jwt;
    private String email;

    private String rol;
}