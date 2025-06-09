package com.example.library.controller;

import com.example.library.entities.repository.AdminRepository;
import com.example.library.entities.repository.ClientRepository;
import com.example.library.entities.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserController {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final AdminRepository adminRepository;

    public UserController(UserRepository userRepository,
                          ClientRepository clientRepository,
                          AdminRepository adminRepository) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.adminRepository = adminRepository;
    }


}
