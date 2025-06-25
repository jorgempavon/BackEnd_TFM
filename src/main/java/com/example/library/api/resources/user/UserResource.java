package com.example.library.api.resources.user;

import com.example.library.api.exceptions.models.UnauthorizedException;
import com.example.library.config.CustomUserDetails;
import com.example.library.entities.dto.user.*;
import com.example.library.services.user.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/bibliokie/users")
public class UserResource {
    private final UserService userService;

    public UserResource(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        UserDTO responseUserDTO = this.userService.findById(id);
        return ResponseEntity.ok(responseUserDTO);
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody UserAdminUpdateDTO userAdminUpdateDTO) {
        UserDTO responseUserDTO = this.userService.update(id,userAdminUpdateDTO);
        return ResponseEntity.ok(responseUserDTO);
    }
    @PutMapping("/myself")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> update(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody UserSelfUpdateDTO userSelfUpdateDTO){
        Long id = userDetails.getId();
        UserDTO userDTO = this.userService.update(id, userSelfUpdateDTO);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> findByNameAndDniAndEmail(@RequestParam(required = false) String name,
                                                             @RequestParam(required = false) String dni,
                                                             @RequestParam(required = false) String email){
        List<UserDTO> listUsers =  this.userService.findByNameAndDniAndEmail(name,dni,email);
        return ResponseEntity.ok(listUsers);
    }

    @PostMapping("/login")
    public ResponseEntity<SessionDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        SessionDTO responseLogin = this.userService.login(loginDTO);
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + responseLogin.getJwt())
                .body(responseLogin);
    }
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/logOut")
    public ResponseEntity<Void> logOut(@RequestHeader(name = "Authorization", required = true) String authHeader){
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            this.userService.logOut(token);
        }else {
            throw new UnauthorizedException("No se ha proporcionado token de sesión");
        }
        return ResponseEntity.ok()
                .build();
    }

}
