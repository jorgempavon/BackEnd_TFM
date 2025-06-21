package com.example.library.services;

import com.example.library.api.exceptions.models.BadRequestException;
import com.example.library.api.exceptions.models.ConflictException;
import com.example.library.config.PasswordService;
import com.example.library.entities.dto.*;
import com.example.library.entities.model.Admin;
import com.example.library.entities.model.Client;
import com.example.library.entities.model.User;
import com.example.library.entities.repository.ClientRepository;
import com.example.library.entities.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class ClientService {
    private final EmailService emailService;
    private final PasswordService passwordService;
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;

    private final String status = "status";
    private final String message = "message";
    private final String rol = "client";

    private final String statusEmail = "statusEmail";
    private final String statusDni = "statusDni";
    public ClientService(UserRepository userRepository,ClientRepository clientRepository,
                         PasswordService passwordService, EmailService emailService){
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.emailService = emailService;
    }
    public Map<String, Object> checkUserExistence(String email, String dni) {
        Map<String, Object> validationResult = new HashMap<>();

        validationResult.put(status, false);
        validationResult.put(statusEmail, false);
        validationResult.put(statusDni, false);


        return validationResult;
    }

    public void processValidationResultMap(String email, String dni, Map<String, Object> validationResult){
        String baseMessage = "proporcionado pertenece a otro usuario. Por favor, inténtelo de nuevo";
        if (email != null && this.userRepository.existsByEmail(email)) {
            validationResult.put(status, true);
            validationResult.put(message, "El email " + baseMessage);
            validationResult.put(statusEmail, true);
            String idUserByEmail = "idUserByEmail";
            validationResult.put(idUserByEmail,this.userRepository.findByEmail(email).get().getId());
        }

        if ((Boolean) validationResult.get(status) && dni!=null && this.userRepository.existsByDni(dni)) {
            validationResult.put(message, "El email y dni " + baseMessage);
        } else if (dni!=null && this.userRepository.existsByDni(dni)) {
            validationResult.put(status, true);
            validationResult.put(message, "El dni " + baseMessage);
            validationResult.put(statusDni, true);
            String idUserByDni = "idUserByDni";
            validationResult.put(idUserByDni,this.userRepository.findByDni(dni).get().getId());
        }
    }
    @Transactional
    public UserDTO create(UserCreateDTO userCreateDTO){
        Map<String, Object> responseExistsUser = this.checkUserExistence(
                userCreateDTO.getEmail(),
                userCreateDTO.getDni()
        );

        if ((Boolean) responseExistsUser.get(status)) {
            throw new BadRequestException((String) responseExistsUser.get(message));
        }
        String generatedPassword = this.passwordService.generateStrongPassword();
        String passwordEncoded = this.passwordService.encodePasswords(generatedPassword);
        UserSaveDTO userSaveDTO = new UserSaveDTO(
                userCreateDTO.getDni(),
                userCreateDTO.getEmail(),
                userCreateDTO.getName(),
                userCreateDTO.getLastName(),
                passwordEncoded,
                rol
        );
        String userFullName = userSaveDTO.getName() +" "+ userSaveDTO.getLastName();
        this.emailService.newAccountEmail(userSaveDTO.getEmail(),userFullName,generatedPassword);

        return this.saveClientUser(userSaveDTO);
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

        Map<String, Object> responseExistsUser = this.checkUserExistence(
                userRegisterDTO.getEmail(),
                userRegisterDTO.getDni()
        );

        if ((Boolean) responseExistsUser.get(status)) {
            throw new BadRequestException((String) responseExistsUser.get(message));
        }

        String passwordEncoded = this.passwordService.encodePasswords(userRegisterDTO.getPassword());
        UserSaveDTO userSaveDTO = new UserSaveDTO(
                userRegisterDTO.getDni(),
                userRegisterDTO.getEmail(),
                userRegisterDTO.getName(),
                userRegisterDTO.getLastName(),
                passwordEncoded,
                rol
        );
        String userFullName = userRegisterDTO.getName()+" "+ userRegisterDTO.getLastName();
        this.emailService.newAccountEmail(userRegisterDTO.getEmail(),userFullName,"");
        return this.saveClientUser(userSaveDTO);
    }

    private UserDTO saveClientUser(UserSaveDTO userSaveDTO){
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

}
