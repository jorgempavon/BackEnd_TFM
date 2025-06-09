package com.example.library.entities.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SessionDTO {
    private String jwt;
    private String email;
}