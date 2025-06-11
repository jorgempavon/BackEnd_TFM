package com.example.library.api.resources;

import com.example.library.services.UserService;
import com.example.library.entities.dto.UserCreateDTO;
import com.example.library.entities.dto.UserDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;


@RestController
@RequestMapping("/bibliokie/users")
@SecurityRequirement(name = "bearerAuth")
public class UserResource {
    private final UserService userService;

    public UserResource(UserService userService){this.userService = userService;}

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        UserDTO responseUserDTO = this.userService.findById(id);
        return ResponseEntity.ok(responseUserDTO);
    }
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        UserDTO responseUserDTO = this.userService.create(userCreateDTO);
        URI location = URI.create("/users/" + responseUserDTO.getId());
        return ResponseEntity.created(location).body(responseUserDTO);
    }
}
