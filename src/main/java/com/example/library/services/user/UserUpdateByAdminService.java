package com.example.library.services.user;

import com.example.library.api.exceptions.models.BadRequestException;
import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.config.PasswordService;
import com.example.library.entities.dto.UserAdminUpdateDTO;
import com.example.library.entities.dto.UserDTO;
import com.example.library.entities.model.User;
import com.example.library.entities.repository.UserRepository;
import com.example.library.services.EmailService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
public class UserUpdateByAdminService {
    private final PasswordService passwordService;
    private final EmailService emailService;
    private final UserValidatorService userValidatorService;
    private final UserRepository userRepository;

    public UserUpdateByAdminService(UserValidatorService userValidatorService,
                                    PasswordService passwordService,
                                    EmailService emailService,
                                    UserRepository userRepository){
        this.emailService = emailService;
        this.userValidatorService = userValidatorService;
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }
    @Transactional
    public UserDTO update(Long id, UserAdminUpdateDTO userAdminUpdateDTO){
        validateDataToUpdate(id,userAdminUpdateDTO);

        User user = this.userRepository.findById(id).get();
        updateUserData(user,userAdminUpdateDTO);
        this.userRepository.save(user);

        return new UserDTO(user.getId(),user.getName(),user.getEmail(),user.getDni(),user.getLastName(),user.getRol());
    }
    private void updateUserData(User user, UserAdminUpdateDTO dto) {
        if (this.userValidatorService.isValidAndChanged(dto.getDni(), user.getDni())) {
            user.setDni(dto.getDni());
        }
        if (this.userValidatorService.isValidAndChanged(dto.getName(), user.getName())) {
            user.setName(dto.getName());
        }
        if (this.userValidatorService.isValidAndChanged(dto.getLastName(), user.getLastName())) {
            user.setLastName(dto.getLastName());
        }
        if (this.userValidatorService.isValidAndChanged(dto.getEmail(), user.getEmail())) {
            updateEmail(user, dto);
        } else if (dto.getResetPassword()) {
            regenerateAndUpdatePassword(user);
        }
    }
    private void updateEmail(User user, UserAdminUpdateDTO dto){
        String fullName = user.getName() + " "+ user.getLastName();
        this.emailService.oldAccountEmail(user.getEmail(),dto.getEmail(),fullName);
        String infoNewPassword = "";
        if(dto.getResetPassword()){
            infoNewPassword = this.passwordService.generateStrongPassword();
            String newEncodedPassword = this.passwordService.encodePasswords(infoNewPassword);
            user.setPassword(newEncodedPassword);
        }
        this.emailService.modifiedAccountEmail(user.getEmail(),dto.getEmail(),user.getName() + " "+
                user.getLastName(),infoNewPassword);
        user.setEmail(dto.getEmail());
    }

    private void regenerateAndUpdatePassword(User user){
        String fullName = user.getName() + " "+ user.getLastName();
        String newPassword = this.passwordService.generateStrongPassword();
        String newEncodedPassword = this.passwordService.encodePasswords(newPassword);
        this.emailService.regeneratedPasswordEmail(user.getEmail(),fullName,newPassword);
        user.setPassword(newEncodedPassword);
    }
    private void validateDataToUpdate(Long id,UserAdminUpdateDTO userAdminUpdateDTO){
        if (!this.userRepository.existsById(id)){
            throw new NotFoundException("No existe ningún usuario con el id: "+id.toString());
        }
        Map<String, Object> responseExistsUser = this.userValidatorService.checkUserExistence(
                userAdminUpdateDTO.getEmail(),
                userAdminUpdateDTO.getDni()
        );
        String statusEmail = "statusEmail",idUserByEmail = "idUserByEmail";
        Boolean existUserWithEmail = (Boolean) responseExistsUser.get(statusEmail)
                && !Objects.equals((Long) responseExistsUser.get(idUserByEmail), id);
        String statusDni = "statusDni",idUserByDni = "idUserByDni";
        Boolean existUserWithDni =  (Boolean) responseExistsUser.get(statusDni)
                && !Objects.equals((Long) responseExistsUser.get(idUserByDni), id);

        if (existUserWithEmail || existUserWithDni) {
            String message = "message";
            throw new BadRequestException((String) responseExistsUser.get(message));
        }
    }
}
