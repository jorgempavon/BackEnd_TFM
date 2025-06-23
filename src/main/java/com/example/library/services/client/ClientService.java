package com.example.library.services.client;

import com.example.library.api.exceptions.models.BadRequestException;
import com.example.library.api.exceptions.models.ConflictException;
import com.example.library.config.PasswordService;
import com.example.library.entities.dto.*;
import com.example.library.entities.model.Client;
import com.example.library.entities.model.User;
import com.example.library.entities.repository.ClientRepository;
import com.example.library.entities.repository.UserRepository;
import com.example.library.services.EmailService;
import com.example.library.services.user.UserValidatorService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
public class ClientService {
    private final UserValidatorService userValidatorService;
    private final EmailService emailService;
    private final PasswordService passwordService;
    private final UserRepository userRepository;
    private final ClientSaveService clientSaveService;
    private final ClientRepository clientRepository;
    private final String message = "message";
    private final String status = "status";

    public ClientService( ClientSaveService clientSaveService,UserValidatorService userValidatorService,
                          UserRepository userRepository, ClientRepository clientRepository,
                          PasswordService passwordService, EmailService emailService){
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.emailService = emailService;
        this.clientSaveService = clientSaveService;
        this.userValidatorService = userValidatorService;
    }

    @Transactional
    public UserDTO create(UserCreateDTO userCreateDTO){
        Map<String, Object> responseExistsUser = this.userValidatorService.checkUserExistence(
                userCreateDTO.getEmail(),
                userCreateDTO.getDni()
        );

        if ((Boolean) responseExistsUser.get(status)) {
            throw new BadRequestException((String) responseExistsUser.get(message));
        }
        String generatedPassword = this.passwordService.generateStrongPassword();
        String rol = "client";
        UserSaveDTO userSaveDTO = this.userValidatorService.buildUserSaveDto(userCreateDTO,generatedPassword, rol);
        String userFullName = userSaveDTO.getName() +" "+ userSaveDTO.getLastName();

        this.emailService.newAccountEmail(userSaveDTO.getEmail(),userFullName,generatedPassword);

        return this.clientSaveService.saveClientUser(userSaveDTO);
    }

    @Transactional
    public void delete(Long id){
        if (this.clientRepository.existsByUserId(id)){
            Client client = this.clientRepository.findByUserId(id).get();
            this.clientRepository.delete(client);
        }

        if (this.userRepository.existsById(id)){
            User user = this.userRepository.findById(id).get();
            String userFullName = user.getName() +" "+ user.getLastName();
            this.emailService.deleteAccountEmail(user.getEmail(),userFullName);
            this.userRepository.delete(user);
        }
    }
    @Transactional
    public UserDTO register(UserRegisterDTO userRegisterDTO){
        if (!Objects.equals(userRegisterDTO.getPassword(), userRegisterDTO.getRepeatPassword())) {
            throw new ConflictException("Las contraseñas proporcionadas no coinciden");
        }

        Map<String, Object> responseExistsUser = this.userValidatorService.checkUserExistence(
                userRegisterDTO.getEmail(),
                userRegisterDTO.getDni()
        );
        if ((Boolean) responseExistsUser.get(status)) {
            throw new BadRequestException((String) responseExistsUser.get(message));
        }

        UserSaveDTO userSaveDTO = this.clientSaveService.buildUserSaveDto(userRegisterDTO);
        String userFullName = userRegisterDTO.getName()+" "+ userRegisterDTO.getLastName();
        this.emailService.newAccountEmail(userRegisterDTO.getEmail(),userFullName,"");

        return this.clientSaveService.saveClientUser(userSaveDTO);
    }
}
