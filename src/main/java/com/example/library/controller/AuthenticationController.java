package com.example.library.controller;
import com.example.library.api.exceptions.BadRequestException;
import com.example.library.api.exceptions.ConflictException;
import com.example.library.entities.dto.UserDTO;
import com.example.library.entities.dto.UserRegisterDTO;
import com.example.library.entities.model.Client;
import com.example.library.entities.model.User;
import com.example.library.entities.repository.AdminRepository;
import com.example.library.entities.repository.ClientRepository;
import com.example.library.entities.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;

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
    private boolean existsUser(String email, String dni){
        return (this.userRepository.existsByEmail(email) || this.userRepository.existsByDni(dni));
    }

    public UserDTO register(UserRegisterDTO userRegisterDTO){
        if (this.existsUser(userRegisterDTO.getEmail(),userRegisterDTO.getDni())){
            throw new BadRequestException("Ya existe un usuario con ese email o DNI.");
        }

        if (!Objects.equals(userRegisterDTO.getPassword(), userRegisterDTO.getRepeatPassword())){
            throw new ConflictException("Las contraseñas proporcionadas no coindicen");
        }

        User newUser = new User();
        newUser.updateFromUserRegisterDTO(userRegisterDTO);
        Client newClient = new Client();
        newClient.setUser(newUser);
        this.userRepository.save(newUser);
        this.clientRepository.save(newClient);

        return newUser.getUserDTO();
    }
}
