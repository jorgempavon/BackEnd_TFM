package com.example.library.controller;

import com.example.library.dto.UserDTO;
import com.example.library.dto.UserRegisterDTO;
import com.example.library.entities.repository.AdminRepository;
import com.example.library.entities.repository.ClientRepository;
import com.example.library.entities.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationController {
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final AdminRepository adminRepository;

    public AuthenticationController(UserRepository userRepository,
                          ClientRepository clientRepository,
                          AdminRepository adminRepository) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.adminRepository = adminRepository;
    }
    public boolean existsUser(String email, String dni){
        return (this.userRepository.existsByEmail(email) || this.userRepository.existsByDni(dni));
    }

    public UserDTO register(UserRegisterDTO userRegisterDTO){
        if (existsUser){
        }
}
