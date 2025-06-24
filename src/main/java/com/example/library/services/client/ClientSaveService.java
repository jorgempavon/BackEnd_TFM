package com.example.library.services.client;

import com.example.library.config.PasswordService;
import com.example.library.entities.dto.user.UserDTO;
import com.example.library.entities.dto.user.UserRegisterDTO;
import com.example.library.entities.dto.user.UserSaveDTO;
import com.example.library.entities.model.user.Client;
import com.example.library.entities.model.user.User;
import com.example.library.entities.repository.user.ClientRepository;
import com.example.library.entities.repository.user.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ClientSaveService {
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final PasswordService passwordService;

    private final String rol = "client";

    public ClientSaveService(UserRepository userRepository, ClientRepository clientRepository,
                             PasswordService passwordService){
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }
    public UserDTO saveClientUser(UserSaveDTO userSaveDTO){
        User user = new User();
        user.setDni(userSaveDTO.getDni());
        user.setEmail(userSaveDTO.getEmail());
        user.setName(userSaveDTO.getName());
        user.setLastName(userSaveDTO.getLastName());
        user.setPassword(userSaveDTO.getPasswordEncoded());
        user.setRol(rol);

        this.userRepository.save(user);
        Client client = new Client();
        client.setUser(user);
        this.clientRepository.save(client);

        return new UserDTO(user.getId(),user.getName(),user.getEmail(),user.getDni(),user.getLastName(),user.getRol());
    }

    public UserSaveDTO buildUserSaveDto(UserRegisterDTO userDTO){
        String passwordEncoded = this.passwordService.encodePasswords(userDTO.getPassword());
        return new UserSaveDTO(
                userDTO.getDni(),
                userDTO.getEmail(),
                userDTO.getName(),
                userDTO.getLastName(),
                passwordEncoded,
                rol
        );
    }

}
