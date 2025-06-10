package com.example.library.controller;
import com.example.library.api.exceptions.models.BadRequestException;
import com.example.library.api.exceptions.models.ConflictException;
import com.example.library.api.exceptions.models.UnauthorizedException;
import com.example.library.config.JwtController;
import com.example.library.entities.dto.LoginDTO;
import com.example.library.entities.dto.SessionDTO;
import com.example.library.entities.dto.UserDTO;
import com.example.library.entities.dto.UserRegisterDTO;
import com.example.library.entities.model.Client;
import com.example.library.entities.model.User;
import com.example.library.entities.repository.ClientRepository;
import com.example.library.entities.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class AuthenticationController {
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtController jwtController;
    private final UserDetailsService userDetailsController;

    @Autowired
    public AuthenticationController(UserRepository userRepository,
                          ClientRepository clientRepository, PasswordEncoder passwordEncoder,
                                    JwtController jwtController,
                                    UserDetailsService userDetailsController) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtController =  jwtController;
        this.userDetailsController=userDetailsController;
    }

    private Map<String, Object> checkUserExistence(String email, String dni) {
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


    @Transactional
    public UserDTO register(UserRegisterDTO userRegisterDTO) {

        Map<String, Object> responseExistsUser = this.checkUserExistence(
                userRegisterDTO.getEmail(),
                userRegisterDTO.getDni()
        );

        if ((Boolean) responseExistsUser.get("status")) {
            throw new BadRequestException((String) responseExistsUser.get("message"));
        }

        if (!Objects.equals(userRegisterDTO.getPassword(), userRegisterDTO.getRepeatPassword())) {
            throw new ConflictException("Las contraseñas proporcionadas no coinciden");
        }

        User newUser = new User();
        String passwordHashed = passwordEncoder.encode(userRegisterDTO.getPassword());
        newUser.setPassword(passwordHashed);
        newUser.updateFromUserRegisterDTO(userRegisterDTO);

        Client newClient = new Client();
        newClient.setUser(newUser);

        this.userRepository.save(newUser);
        this.clientRepository.save(newClient);

        return newUser.getUserDTO(false);
    }

    public SessionDTO login(LoginDTO loginDTO){
        UserDetails userDetails = this.userDetailsController.loadUserByUsername(loginDTO.getEmail());
        if (!passwordEncoder.matches(loginDTO.getPassword(), userDetails.getPassword())) {
            throw new UnauthorizedException("El email o contraseña proporcionados son incorrectos");
        }
        String jwt = jwtController.generateToken(userDetails);

        SessionDTO responseLogin = new SessionDTO();
        responseLogin.setEmail(userDetails.getUsername());
        responseLogin.setJwt(jwt);

        return responseLogin;
    }
}
