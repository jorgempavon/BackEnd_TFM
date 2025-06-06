package com.example.library.controller;

import com.example.library.model.Admin;
import com.example.library.model.Client;
import com.example.library.model.User;
import com.example.library.repository.AdminRepository;
import com.example.library.repository.ClientRepository;
import com.example.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/bibliokie/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private AdminRepository adminRepository;
    @GetMapping
    public List<User> UserHome(){
        return this.userRepository.findAll();
    }

    @GetMapping("/clients")
    public List<Client> ClientsHome(){
        return clientRepository.findAll();
    }

    @GetMapping("/admin")
    public List<Admin> Adminhome(){
        return adminRepository.findAll();
    }
}
