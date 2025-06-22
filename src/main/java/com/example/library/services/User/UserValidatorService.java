package com.example.library.services.User;

import com.example.library.config.PasswordService;
import com.example.library.entities.dto.UserCreateDTO;
import com.example.library.entities.dto.UserSaveDTO;
import com.example.library.entities.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserValidatorService {
    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final String status = "status";
    private final String message = "message";
    private final String statusEmail = "statusEmail";
    private final String statusDni = "statusDni";
    private final String baseMessage = "proporcionado pertenece a otro usuario. Por favor, inténtelo de nuevo";

    public UserValidatorService(UserRepository userRepository,PasswordService passwordService){
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }

    public Map<String, Object> checkUserExistence(String email, String dni) {
        Map<String, Object> validationResult = new HashMap<>();

        validationResult.put(status, false);
        validationResult.put(statusEmail, false);
        validationResult.put(statusDni, false);

        if (email != null && this.userRepository.existsByEmail(email)) {
            buildEmailValidation(validationResult,email);
        }

        if ((Boolean) validationResult.get(status) && dni!=null && this.userRepository.existsByDni(dni)) {
            validationResult.put(message, "El email y dni " + baseMessage);
        } else if (dni!=null && this.userRepository.existsByDni(dni)) {
            buildDniValidation(validationResult,dni);
        }

        return validationResult;
    }

    private void buildEmailValidation(Map<String, Object> validationResult,String email){
        validationResult.put(status, true);
        validationResult.put(message, "El email " + baseMessage);
        validationResult.put(statusEmail, true);
        String idUserByEmail = "idUserByEmail";
        validationResult.put(idUserByEmail,this.userRepository.findByEmail(email).get().getId());
    }
    private void buildDniValidation(Map<String, Object> validationResult,String dni){
        validationResult.put(status, true);
        validationResult.put(message, "El dni " + baseMessage);
        validationResult.put(statusDni, true);
        String idUserByDni = "idUserByDni";
        validationResult.put(idUserByDni,this.userRepository.findByDni(dni).get().getId());
    }
    public boolean isValidAndChanged(String newValue, String oldValue) {
        return newValue != null && !newValue.isEmpty() && !newValue.equals(oldValue);
    }

    public UserSaveDTO buildUserSaveDto(UserCreateDTO userDTO, String password, String rol){
        String passwordEncoded = this.passwordService.encodePasswords(password);
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
