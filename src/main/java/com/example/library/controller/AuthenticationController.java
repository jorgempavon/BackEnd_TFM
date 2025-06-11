package com.example.library.controller;
import com.example.library.api.exceptions.models.ConflictException;
import com.example.library.api.exceptions.models.UnauthorizedException;
import com.example.library.config.JwtController;
import com.example.library.entities.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Objects;

@Service
public class AuthenticationController {
    private final UserController userController;
    private final PasswordEncoder passwordEncoder;
    private final JwtController jwtController;
    private final UserDetailsService userDetailsController;

    @Autowired
    public AuthenticationController(UserController userController,
                                    PasswordEncoder passwordEncoder,
                                    JwtController jwtController,
                                    UserDetailsService userDetailsController) {
        this.userController = userController;
        this.jwtController =  jwtController;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsController=userDetailsController;
    }

    public UserDTO register(UserRegisterDTO userRegisterDTO) {
        if (!Objects.equals(userRegisterDTO.getPassword(), userRegisterDTO.getRepeatPassword())) {
            throw new ConflictException("Las contraseñas proporcionadas no coinciden");
        }
        return this.userController.create(userRegisterDTO);
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
