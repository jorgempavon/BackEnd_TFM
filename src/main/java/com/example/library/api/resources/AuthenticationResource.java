package com.example.library.api.resources;

import com.example.library.api.exceptions.models.UnauthorizedException;
import com.example.library.entities.dto.LoginDTO;
import com.example.library.entities.dto.SessionDTO;
import com.example.library.entities.dto.UserDTO;
import com.example.library.entities.dto.UserRegisterDTO;
import com.example.library.services.AuthenticationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
    public ResponseEntity<SessionDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        SessionDTO responseLogin = this.authenticationService.login(loginDTO);
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + responseLogin.getJwt())
                .body(responseLogin);
    }
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/logOut")
    public ResponseEntity<Void> logOut(@RequestHeader(name = "Authorization", required = true) String authHeader){
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            this.authenticationService.logOut(token);
        }else {
            throw new UnauthorizedException("No se ha proporcionado token de sesión");
        }
        return ResponseEntity.ok()
                .build();
    }
}
