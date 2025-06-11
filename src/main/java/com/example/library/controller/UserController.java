package com.example.library.controller;

import com.example.library.api.exceptions.models.BadRequestException;
import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.entities.dto.UserCreateDTO;
import com.example.library.entities.dto.UserDTO;
import com.example.library.entities.dto.UserRegisterDTO;
import com.example.library.entities.dto.UserSaveDTO;
import com.example.library.entities.model.Admin;
import com.example.library.entities.model.Client;
import com.example.library.entities.model.User;
import com.example.library.entities.repository.AdminRepository;
import com.example.library.entities.repository.ClientRepository;
import com.example.library.entities.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserController {
    private final JavaMailSender mailSender;
    private final PasswordGenerator passwordGenerator;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final AdminRepository adminRepository;

    public UserController(UserRepository userRepository,
                          ClientRepository clientRepository,
                          AdminRepository adminRepository,
                          PasswordEncoder passwordEncoder,
                          PasswordGenerator passwordGenerator,
                          JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordGenerator = passwordGenerator;
        this.mailSender = mailSender;
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
        String generatedPassword = this.passwordGenerator.generateStrongPassword();
        String passwordEncoded = passwordEncoder.encode(generatedPassword);
        UserSaveDTO userSaveDTO = new UserSaveDTO(
                userCreateDTO.getDni(),
                userCreateDTO.getEmail(),
                userCreateDTO.getName(),
                userCreateDTO.getLastName(),
                passwordEncoded,
                userCreateDTO.getIsAdmin()
        );
        String passwordText = ",para acceder utilice la siguiente contraseña: "+ generatedPassword
                + "\nPorfavor, le recomendamos cambiar la contraseña lo antes posible";
        this.sendNewAccountEmail(userCreateDTO.getEmail(),passwordText);
        return this.save(userSaveDTO);
    }

    public UserDTO create(UserRegisterDTO userRegisterDTO){
        Map<String, Object> responseExistsUser = this.checkUserExistence(
                userRegisterDTO.getEmail(),
                userRegisterDTO.getDni()
        );

        if ((Boolean) responseExistsUser.get("status")) {
            throw new BadRequestException((String) responseExistsUser.get("message"));
        }

        String passwordEncoded = passwordEncoder.encode(userRegisterDTO.getPassword());
        UserSaveDTO userSaveDTO = new UserSaveDTO(
                userRegisterDTO.getDni(),
                userRegisterDTO.getEmail(),
                userRegisterDTO.getName(),
                userRegisterDTO.getLastName(),
                passwordEncoded,
                false
        );

        this.sendNewAccountEmail(userRegisterDTO.getEmail(),"");
        return this.save(userSaveDTO);
    }
    @Transactional
    private UserDTO save(UserSaveDTO userSaveDTO){
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
            client.setUser(user);
            this.clientRepository.save(client);
        }

        return user.getUserDTO(isAdmin);
    }
    private void sendNewAccountEmail(String email, String passwordText) {
        try{
            String subject = "Nueva Cuenta en Bibliokie";
            String body = "Ha sido dado de alta en Bibliokie" +
                    passwordText;
            String endBody="\nEste correro es meramente informativo.\n\nMuchas gracias,\nUn saludo.";

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("bibliokiejackie@gmail.com");
            message.setTo(email);
            message.setSubject(subject);
            message.setText(body + endBody);
            mailSender.send(message);
        }
        catch (Exception e){
            throw new BadRequestException("El correo proporcionado no existe. Porfavor, introduzca un nuevo.");
        }
    }
}
