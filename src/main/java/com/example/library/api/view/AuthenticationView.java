package com.example.library.api.view;

import com.example.library.entities.dto.LoginDTO;
import com.example.library.entities.dto.SessionDTO;
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

    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginDTO loginDTO) {
        SessionDTO responseLogin = this.authController.login(loginDTO);
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + responseLogin.getJwt())
                .build();
    }
}
