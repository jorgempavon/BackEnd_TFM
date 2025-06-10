package com.example.library.controller;

import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.entities.dto.UserDTO;
import com.example.library.entities.model.User;
import com.example.library.entities.repository.AdminRepository;
import com.example.library.entities.repository.ClientRepository;
import com.example.library.entities.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    public UserDTO findById(Long id){
        if(!this.userRepository.existsById(id)){
            throw new NotFoundException("No existe ningún usuario con el id: "+id.toString());
        }
        User user = this.userRepository.findById(id).get();
        boolean isAdmin = false;
        if(this.adminRepository.existsByUserId(id)){
            isAdmin = true;
        }

        return user.getUserDTO(isAdmin);
    }
}
