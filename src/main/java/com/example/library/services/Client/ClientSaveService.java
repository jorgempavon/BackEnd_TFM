package com.example.library.services.Client;

import com.example.library.config.PasswordService;
import com.example.library.entities.dto.UserDTO;
import com.example.library.entities.dto.UserRegisterDTO;
import com.example.library.entities.dto.UserSaveDTO;
import com.example.library.entities.model.Client;
import com.example.library.entities.model.User;
import com.example.library.entities.repository.ClientRepository;
import com.example.library.entities.repository.UserRepository;
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
