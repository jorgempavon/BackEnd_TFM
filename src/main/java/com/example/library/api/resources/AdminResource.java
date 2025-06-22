package com.example.library.api.resources;

import com.example.library.entities.dto.UserCreateDTO;
import com.example.library.entities.dto.UserDTO;
import com.example.library.services.Admin.AdminService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/bibliokie/users/admin")
public class AdminResource {
    private final AdminService adminService;

    public AdminResource(AdminService adminService){
        this.adminService = adminService;
    }
    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        UserDTO responseUserDTO = this.adminService.create(userCreateDTO);
        URI location = URI.create("/users/" + responseUserDTO.getId());
        return ResponseEntity.created(location).body(responseUserDTO);
    }
    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        this.adminService.delete(id);
        return ResponseEntity.ok().build();
    }

}
