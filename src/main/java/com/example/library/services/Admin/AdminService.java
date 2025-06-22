package com.example.library.services.Admin;
import com.example.library.api.exceptions.models.BadRequestException;
import com.example.library.config.PasswordService;
import com.example.library.entities.dto.UserCreateDTO;
import com.example.library.entities.dto.UserDTO;
import com.example.library.entities.dto.UserSaveDTO;
import com.example.library.entities.model.Admin;
import com.example.library.entities.model.User;
import com.example.library.entities.repository.AdminRepository;
import com.example.library.entities.repository.UserRepository;
import com.example.library.services.EmailService;
import com.example.library.services.User.UserValidatorService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.Map;
@Service
public class AdminService {
    private final UserValidatorService userValidatorService;
    private final EmailService emailService;
    private final PasswordService passwordService;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    public AdminService( UserValidatorService userValidatorService,
                          UserRepository userRepository, AdminRepository adminRepository,
                          PasswordService passwordService, EmailService emailService){
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.emailService = emailService;
        this.userValidatorService = userValidatorService;
    }

    @Transactional
    public UserDTO create(UserCreateDTO userCreateDTO){
        Map<String, Object> responseExistsUser = this.userValidatorService.checkUserExistence(
                userCreateDTO.getEmail(),
                userCreateDTO.getDni()
        );

        String status = "status";
        if ((Boolean) responseExistsUser.get(status)) {
            String message = "message";
            throw new BadRequestException((String) responseExistsUser.get(message));
        }
        String generatedPassword = this.passwordService.generateStrongPassword();
        String rol = "admin";
        UserSaveDTO userSaveDTO = this.userValidatorService.buildUserSaveDto(userCreateDTO,generatedPassword,rol);
        String userFullName = userSaveDTO.getName() +" "+ userSaveDTO.getLastName();

        this.emailService.newAccountEmail(userSaveDTO.getEmail(),userFullName,generatedPassword);

        return this.saveAdminUser(userSaveDTO);
    }
    private UserDTO saveAdminUser(UserSaveDTO userSaveDTO){
        User user = new User();
        user.setDni(userSaveDTO.getDni());
        user.setEmail(userSaveDTO.getEmail());
        user.setName(userSaveDTO.getName());
        user.setLastName(userSaveDTO.getLastName());
        user.setPassword(userSaveDTO.getPasswordEncoded());
        String rol = "admin";
        user.setRol(rol);

        this.userRepository.save(user);
        Admin admin = new Admin();
        admin.setUser(user);
        this.adminRepository.save(admin);

        return new UserDTO(user.getId(),user.getName(),user.getEmail(),user.getDni(),user.getLastName(),user.getRol());
    }
    @Transactional
    public void delete(Long id){
        if (this.adminRepository.existsByUserId(id)){
            Admin admin = this.adminRepository.findByUserId(id).get();
            this.adminRepository.delete(admin);
        }

        if (this.userRepository.existsById(id)){
            User user = this.userRepository.findById(id).get();
            String userFullName = user.getName() +" "+ user.getLastName();
            this.emailService.deleteAccountEmail(user.getEmail(),userFullName);
            this.userRepository.delete(user);
        }
    }
}
