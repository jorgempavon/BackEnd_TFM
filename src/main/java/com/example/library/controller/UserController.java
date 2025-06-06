package com.example.library.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bibliokie/users")
public class UserController {

    @GetMapping
    public String UserHome(){
        return "Aló desde controlador";
    }
}
