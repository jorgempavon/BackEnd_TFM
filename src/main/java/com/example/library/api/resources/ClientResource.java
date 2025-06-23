package com.example.library.api.resources;

import com.example.library.entities.dto.UserCreateDTO;
import com.example.library.entities.dto.UserDTO;
import com.example.library.entities.dto.UserRegisterDTO;
import com.example.library.services.client.ClientService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.net.URI;

@RestController
@RequestMapping("/bibliokie/users/client")
public class ClientResource {

    private final ClientService clientService;

    public ClientResource(ClientService clientService){
        this.clientService = clientService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterDTO newUser){
        UserDTO created = clientService.register(newUser);
        URI location = URI.create("/users/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }
    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        UserDTO responseUserDTO = this.clientService.create(userCreateDTO);
        URI location = URI.create("/users/" + responseUserDTO.getId());
        return ResponseEntity.created(location).body(responseUserDTO);
    }
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        this.clientService.delete(id);
        return ResponseEntity.ok().build();
    }

}
