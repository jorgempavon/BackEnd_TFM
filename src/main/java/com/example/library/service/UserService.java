package com.example.library.service;

import com.example.library.repository.AdminRepository;
import com.example.library.repository.ClientRepository;
import com.example.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private AdminRepository adminRepository;


}
