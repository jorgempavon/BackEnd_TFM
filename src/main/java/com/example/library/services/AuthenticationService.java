package com.example.library.services;
import com.example.library.api.exceptions.models.ConflictException;
import com.example.library.api.exceptions.models.UnauthorizedException;
import com.example.library.config.JwtService;
import com.example.library.entities.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Objects;

@Service
public class AuthenticationService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsController;

    @Autowired
    public AuthenticationService(UserService userService,
                                 PasswordEncoder passwordEncoder,
                                 JwtService jwtService,
                                 UserDetailsService userDetailsController) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsController=userDetailsController;
    }

    public UserDTO register(UserRegisterDTO userRegisterDTO) {
        if (!Objects.equals(userRegisterDTO.getPassword(), userRegisterDTO.getRepeatPassword())) {
            throw new ConflictException("Las contraseñas proporcionadas no coinciden");
        }
        return this.userService.create(userRegisterDTO);
    }

    public SessionDTO login(LoginDTO loginDTO){
        UserDetails userDetails = this.userDetailsController.loadUserByUsername(loginDTO.getEmail());
        if (!passwordEncoder.matches(loginDTO.getPassword(), userDetails.getPassword())) {
            throw new UnauthorizedException("El email o contraseña proporcionados son incorrectos");
        }
        String jwt = jwtService.generateToken(userDetails);

        SessionDTO responseLogin = new SessionDTO();
        responseLogin.setEmail(userDetails.getUsername());
        responseLogin.setJwt(jwt);

        return responseLogin;
    }

    public void logOut(String token){
        String userName = this.jwtService.extractUsername(token);
        UserDetails userDetails = this.userDetailsController.loadUserByUsername(userName);
        if (this.jwtService.isTokenValid(token,userDetails)){
            this.jwtService.invalidateToken(token);
        }
    }
}
