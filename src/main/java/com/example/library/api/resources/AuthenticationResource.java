package com.example.library.api.resources;

import com.example.library.entities.dto.LoginDTO;
import com.example.library.entities.dto.SessionDTO;
import com.example.library.entities.dto.UserDTO;
import com.example.library.entities.dto.UserRegisterDTO;
import com.example.library.services.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;

@RestController
@RequestMapping("/bibliokie/authentication")
public class AuthenticationResource {

    private final AuthenticationService authenticationService;

    public AuthenticationResource(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody  UserRegisterDTO newUser){
        UserDTO created = authenticationService.register(newUser);
        URI location = URI.create("/users/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginDTO loginDTO) {
        SessionDTO responseLogin = this.authenticationService.login(loginDTO);
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + responseLogin.getJwt())
                .build();
    }
}
