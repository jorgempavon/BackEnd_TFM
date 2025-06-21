package com.example.library.api.resources;

import com.example.library.entities.dto.UserAdminUpdateDTO;
import com.example.library.entities.dto.UserCreateDTO;
import com.example.library.entities.dto.UserDTO;
import com.example.library.entities.dto.UserRegisterDTO;
import com.example.library.services.ClientService;
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        UserDTO responseUserDTO = this.clientService.create(userCreateDTO);
        URI location = URI.create("/users/" + responseUserDTO.getId());
        return ResponseEntity.created(location).body(responseUserDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody UserAdminUpdateDTO userAdminUpdateDTO){
        UserDTO userDTO = this.clientService.update(id,userAdminUpdateDTO);
        return ResponseEntity.ok(userDTO);
    }

    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        this.clientService.delete(id);
        return ResponseEntity.ok().build();
    }

}
