package com.example.library.api.view;

import com.example.library.controller.UserController;
import com.example.library.entities.dto.UserDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/bibliokie/users")
@SecurityRequirement(name = "bearerAuth")
public class UserView {
    private final UserController userController;

    public UserView(UserController userController){this.userController=userController;}

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        UserDTO responseUserDTO = this.userController.findById(id);
        return ResponseEntity.ok(responseUserDTO);
    }
}
