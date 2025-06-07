package com.example.library.controller;

import com.example.library.dto.UserRegisterDTO;

import com.example.library.repository.UserRepository;
import com.example.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bibliokie/authentication")
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping("/register")
    public ResponseEntity<?> Register(@RequestBody UserRegisterDTO newUser){
        boolean existEmail = this.userRepository.existsByEmail(newUser.getEmail()),
                existDni = this.userRepository.existsByDni(newUser.getDni()),
                existsUser = existDni || existEmail;

        if (existsUser){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Ya existe el DNI o Email proporcionado");
        }


    }



}
