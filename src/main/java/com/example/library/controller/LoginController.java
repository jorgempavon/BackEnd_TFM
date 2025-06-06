package com.example.library.controller;

import com.example.library.dto.UserRegisterDTO;
import com.example.library.model.User;
import com.example.library.repository.AdminRepository;
import com.example.library.repository.ClientRepository;
import com.example.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bibliokie/authentication")
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> Register(@RequestBody UserRegisterDTO newUser){
        if (this.existsUser(newUser.getEmail(),newUser.getDni())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error, el email o Dni proporcionados ya existen.");
        }


    }

    public boolean isNewUserDataValid(){

    }
    private boolean existsUser(String email,String dni){
        return this.userRepository.existsByDni(dni) || this.userRepository.existsByEmail(email);
    }
}
