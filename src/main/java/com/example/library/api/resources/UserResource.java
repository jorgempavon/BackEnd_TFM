package com.example.library.api.resources;

import com.example.library.entities.dto.UserUpdateDTO;
import com.example.library.services.UserService;
import com.example.library.entities.dto.UserCreateDTO;
import com.example.library.entities.dto.UserDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        UserDTO responseUserDTO = this.userService.create(userCreateDTO);
        URI location = URI.create("/users/" + responseUserDTO.getId());
        return ResponseEntity.created(location).body(responseUserDTO);
    }
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        this.userService.delete(id);
        return ResponseEntity.ok().build();
    }
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO userUpdateDTO){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin){
            userUpdateDTO.setPassword(null);
            userUpdateDTO.setRepeatPassword(null);
        } else {
            userUpdateDTO.setIsAdmin(null);
        }

        UserDTO userDTO = this.userService.update(id,userUpdateDTO);
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
