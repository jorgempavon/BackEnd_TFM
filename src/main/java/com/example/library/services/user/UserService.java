package com.example.library.services.user;
import com.example.library.api.exceptions.models.BadRequestException;
import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.api.exceptions.models.UnauthorizedException;
import com.example.library.config.CustomUserDetailsService;
import com.example.library.config.JwtService;
import com.example.library.config.PasswordService;
import com.example.library.entities.dto.user.*;
import com.example.library.entities.model.user.Client;
import com.example.library.entities.model.user.User;
import com.example.library.entities.repository.user.UserRepository;
import com.example.library.services.EmailService;
import com.example.library.util.ValidationUtils;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final  EmailService emailService;
    private final PasswordService passwordService;

    private final UserValidatorService userValidatorService;

    public UserService(UserValidatorService userValidatorService,PasswordService passwordService,
                       CustomUserDetailsService userDetailsService, UserRepository userRepository, EmailService emailService,
                       JwtService jwtService) {
        this.emailService = emailService;
        this.passwordService = passwordService;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.userValidatorService = userValidatorService;
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

        spec = ValidationUtils.buildQueryUserStringByField(spec,"name",name);
        spec = ValidationUtils.buildQueryUserStringByField(spec,"dni",name);
        spec = ValidationUtils.buildQueryUserStringByField(spec,"email",name);

        List<User> users = this.userRepository.findAll(spec);
        List<UserDTO> responseList= new ArrayList<>();

        for (User user : users) {
            UserDTO newUserDto = new UserDTO(user.getId(),user.getName(),user.getEmail(),user.getDni(),user.getLastName(),user.getRol());
            responseList.add(newUserDto);
        }

        return responseList;
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
    @Transactional
    public UserAndUserDTO create(UserCreateDTO userCreateDTO,String rol, String password){
        UserExistenceDTO responseExistsUser = this.userValidatorService.checkUserExistence(userCreateDTO.getEmail(),
                userCreateDTO.getDni());
        if (responseExistsUser.getStatus()) {
            throw new BadRequestException(responseExistsUser.getMessage());
        }

        String userFullName = userCreateDTO.getName() +" "+ userCreateDTO.getLastName();
        if (password.isBlank()){
            password = this.passwordService.generateStrongPassword();
            this.emailService.newAccountEmail(userCreateDTO.getEmail(),userFullName,password);
        }else {
            this.emailService.newAccountEmail(userCreateDTO.getEmail(),userFullName,"");
        }

        UserSaveDTO userSaveDTO = this.buildUserSaveDto(userCreateDTO,password, rol);
        User user = new User(userSaveDTO.getName(),userSaveDTO.getDni(),userSaveDTO.getEmail(),
                userSaveDTO.getLastName());
        user.setPassword(userSaveDTO.getPasswordEncoded());
        user.setRol(rol);
        this.userRepository.save(user);

        UserDTO userDTO = new UserDTO(user.getId(),user.getName(),user.getEmail(),user.getDni(),
                user.getLastName(),user.getRol());
        return new UserAndUserDTO(user, userDTO);
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
    @Transactional
    public UserDTO update(Long id, UserSelfUpdateDTO userSelfUpdateDTO){
        this.userValidatorService.validateDataInSelfUpdate(id, userSelfUpdateDTO);

        User user = this.userRepository.findById(id).get();
        this.userValidatorService.updateUserDataInSelfUpdate(user,userSelfUpdateDTO);

        if (userSelfUpdateDTO.getPassword() != null && !userSelfUpdateDTO.getPassword().isBlank()) {
            String encodedPassword = this.passwordService.encodePasswords(userSelfUpdateDTO.getPassword());
            user.setPassword(encodedPassword);
        }
        this.userRepository.save(user);
        return new UserDTO(user.getId(),user.getName(),user.getEmail(),user.getDni(),user.getLastName(),user.getRol());
    }
    @Transactional
    public UserDTO update(Long id, UserAdminUpdateDTO userAdminUpdateDTO){
        this.userValidatorService.validateDataToUpdateInUpdateByAdmin(id,userAdminUpdateDTO);

        User user = this.userRepository.findById(id).get();
        this.userValidatorService.updateUserDataInUpdateByAdmin(user,userAdminUpdateDTO);
        this.userRepository.save(user);

        return new UserDTO(user.getId(),user.getName(),user.getEmail(),user.getDni(),user.getLastName(),user.getRol());
    }

    @Transactional
    public void delete(Long id){
        if (this.userRepository.existsById(id)){
            User user = this.userRepository.findById(id).get();
            this.userRepository.delete(user);
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
