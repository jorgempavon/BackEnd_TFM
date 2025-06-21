package com.example.library.api.resources;

import com.example.library.config.CustomUserDetails;
import com.example.library.entities.dto.UserAdminUpdateDTO;
import com.example.library.entities.dto.UserSelfUpdateDTO;
import com.example.library.services.UserService;
import com.example.library.entities.dto.UserCreateDTO;
import com.example.library.entities.dto.UserDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("/bibliokie/users")
@SecurityRequirement(name = "bearerAuth")
public class UserResource {
    private final UserService userService;

    public UserResource(UserService userService){this.userService = userService;}
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        UserDTO responseUserDTO = this.userService.findById(id);
        return ResponseEntity.ok(responseUserDTO);
    }

    @PutMapping("/myself")
    public ResponseEntity<?> update(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody UserSelfUpdateDTO userSelfUpdateDTO){
        Long id = userDetails.getId();
        UserDTO userDTO = this.userService.update(id, userSelfUpdateDTO);
        return ResponseEntity.ok(userDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<?> findByNameAndDniAndEmail(@RequestParam(required = false) String name,
                                                             @RequestParam(required = false) String dni,
                                                             @RequestParam(required = false) String email){
        List<UserDTO> listUsers =  this.userService.findByNameAndDniAndEmail(name,dni,email);
        return ResponseEntity.ok(listUsers);
    }
}
