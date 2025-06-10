package com.example.library.controller;

import com.example.library.api.exceptions.models.BadRequestException;
import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.entities.dto.UserCreateDTO;
import com.example.library.entities.dto.UserDTO;
import com.example.library.entities.dto.UserSaveDTO;
import com.example.library.entities.model.Admin;
import com.example.library.entities.model.Client;
import com.example.library.entities.model.User;
import com.example.library.entities.repository.AdminRepository;
import com.example.library.entities.repository.ClientRepository;
import com.example.library.entities.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserController {

    private final PasswordEncoder passwordEncoder;
    private final EmailController emailController;
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final AdminRepository adminRepository;

    public UserController(UserRepository userRepository,
                          ClientRepository clientRepository,
                          AdminRepository adminRepository,
                          EmailController emailController,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.adminRepository = adminRepository;
        this.emailController = emailController;
        this.passwordEncoder = passwordEncoder;
    }
    public Map<String, Object> checkUserExistence(String email, String dni) {
        Map<String, Object> validationResult = new HashMap<>();

        String baseMessage = "proporcionado pertenece a otro usuario. Por favor, inténtelo de nuevo";

        validationResult.put("status", false);

        if (this.userRepository.existsByEmail(email)) {
            validationResult.put("status", true);
            validationResult.put("message", "El email " + baseMessage);
        }

        if ((Boolean) validationResult.get("status") && this.userRepository.existsByDni(dni)) {
            validationResult.put("message", "El email y dni " + baseMessage);
        } else if (this.userRepository.existsByDni(dni)) {
            validationResult.put("status", true);
            validationResult.put("message", "El dni " + baseMessage);
        }

        return validationResult;
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

    public UserDTO create(UserCreateDTO userCreateDTO){
        Map<String, Object> responseExistsUser = this.checkUserExistence(
                userCreateDTO.getEmail(),
                userCreateDTO.getDni()
        );

        if ((Boolean) responseExistsUser.get("status")) {
            throw new BadRequestException((String) responseExistsUser.get("message"));
        }

        String passwordEncoded = passwordEncoder.encode(this.generateStrongPassword());
        UserSaveDTO userSaveDTO = new UserSaveDTO(
                userCreateDTO.getDni(),
                userCreateDTO.getEmail(),
                userCreateDTO.getName(),
                userCreateDTO.getLastName(),
                passwordEncoded,
                false
        );
        return this.create(userSaveDTO);
    }

    @Transactional
    public UserDTO create(UserSaveDTO userSaveDTO){
        boolean isAdmin = userSaveDTO.getIsAdmin();
        User user = new User();
        user.updateFromUserSaveDTO(userSaveDTO);
        this.userRepository.save(user);

        if (isAdmin){
            Admin admin = new Admin();
            admin.setUser(user);
            this.adminRepository.save(admin);
        }
        else {
            Client client = new Client();
            this.clientRepository.save(client);
            client.setUser(user);
        }

        return user.getUserDTO(isAdmin);
    }
    public UserDTO sendEmail(String email, String password){
        String subject = "Nueva Cuenta en Bibliokie";
        String body = "Ha sido dado de alta en la aplicación Bibliokie," +
                " su nueva contraseña es la siguiente: "+password;

        this.emailController.sendSimpleMessage(email,subject,body);
        return new UserDTO();
    }

    private String generateStrongPassword(){
        return "root";
    }
}
