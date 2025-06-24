package com.example.library.services.user;
import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.api.exceptions.models.UnauthorizedException;
import com.example.library.config.CustomUserDetailsService;
import com.example.library.config.JwtService;
import com.example.library.config.PasswordService;
import com.example.library.entities.dto.user.LoginDTO;
import com.example.library.entities.dto.user.SessionDTO;
import com.example.library.entities.dto.user.UserDTO;
import com.example.library.entities.model.user.User;
import com.example.library.entities.repository.user.UserRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    private final PasswordService passwordService;


    public UserService( PasswordService passwordService,
                        CustomUserDetailsService userDetailsService,
                        UserRepository userRepository,
                       JwtService jwtService) {
        this.passwordService = passwordService;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public UserDTO findById(Long id){
        if(!this.userRepository.existsById(id)){
            throw new NotFoundException("No existe ningún usuario con el id: "+id.toString());
        }
        User user = this.userRepository.findById(id).get();
        return new UserDTO(user.getId(),user.getName(),user.getEmail(),user.getDni(),user.getLastName(),user.getRol());
    }

    public List<UserDTO> findByNameAndDniAndEmail(String name, String dni, String email) {
        Specification<User> spec = Specification.where(null);

        this.buildQueryByField(spec,"name",name);
        this.buildQueryByField(spec,"dni",name);
        this.buildQueryByField(spec,"email",name);

        List<User> users = this.userRepository.findAll(spec);
        List<UserDTO> responseList= new ArrayList<>();

        for (User user : users) {
            UserDTO newUserDto = new UserDTO(user.getId(),user.getName(),user.getEmail(),user.getDni(),user.getLastName(),user.getRol());
            responseList.add(newUserDto);
        }

        return responseList;
    }

    private void buildQueryByField(Specification<User> spec,String field, String value){
        if (value != null && !value.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get(field)), "%" + value.toLowerCase() + "%")
            );
        }
    }

    public SessionDTO login(LoginDTO loginDTO){
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(loginDTO.getEmail());
        if (!passwordService.matchesPasswords(loginDTO.getPassword(), userDetails.getPassword())) {
            throw new UnauthorizedException("El email o contraseña proporcionados son incorrectos");
        }
        String jwt = jwtService.generateToken(userDetails);
        String userEmail = userDetails.getUsername();
        User user = this.userRepository.findByEmail(userEmail).get();

        SessionDTO responseLogin = new SessionDTO();
        responseLogin.setRol(user.getRol());
        responseLogin.setId(user.getId());
        responseLogin.setEmail(userEmail);
        responseLogin.setJwt(jwt);

        return responseLogin;
    }

    public void logOut(String token){
        String userName = this.jwtService.extractUsername(token);
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(userName);
        if (this.jwtService.isTokenValid(token,userDetails)){
            this.jwtService.invalidateToken(token);
        }
    }

    public String getUserFullName(User user){
        if(!this.userRepository.existsById(user.getId())){
            throw new NotFoundException("No existe el usuario proporcionado");
        }
        return user.getName()+" "+user.getLastName();
    }

    public String getUserEmail(User user){
        if(!this.userRepository.existsById(user.getId())){
            throw new NotFoundException("No existe el usuario proporcionado");
        }
        return user.getEmail();
    }
}
