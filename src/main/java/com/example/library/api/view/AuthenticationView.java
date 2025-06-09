package com.example.library.api.view;

import com.example.library.entities.dto.UserDTO;
import com.example.library.entities.dto.UserRegisterDTO;
import com.example.library.controller.AuthenticationController;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/bibliokie/authentication")
public class AuthenticationView {

    private final AuthenticationController authController;

    public AuthenticationView(AuthenticationController authController) {
        this.authController = authController;
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody  UserRegisterDTO newUser){
        UserDTO created = authController.register(newUser);
        URI location = URI.create("/users/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }



}
