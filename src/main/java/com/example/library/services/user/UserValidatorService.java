package com.example.library.services.user;

import com.example.library.api.exceptions.models.BadRequestException;
import com.example.library.api.exceptions.models.ConflictException;
import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.config.PasswordService;
import com.example.library.entities.dto.user.UserAdminUpdateDTO;
import com.example.library.entities.dto.user.UserExistenceDTO;
import com.example.library.entities.dto.user.UserSelfUpdateDTO;
import com.example.library.entities.model.user.User;
import com.example.library.entities.repository.user.UserRepository;
import com.example.library.services.EmailService;
import com.example.library.util.ValidationUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserValidatorService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordService passwordService;


    public UserValidatorService(UserRepository userRepository,EmailService emailService,
                                PasswordService passwordService){
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordService = passwordService;
    }


    public UserExistenceDTO checkUserExistence(String email, String dni) {
        UserExistenceDTO validationResult = new UserExistenceDTO();

        String baseMessage = "proporcionado pertenece a otro usuario. Por favor, inténtelo de nuevo";
        if (email != null && this.userRepository.existsByEmail(email)) {
            validationResult.setMessage("El email " + baseMessage);
            validationResult.setStatusEmail(true);
            validationResult.setStatus(true);
            validationResult.setIdUserByEmail(this.userRepository.findByEmail(email).get().getId());
        }

        if (validationResult.getStatus() && dni!=null && this.userRepository.existsByDni(dni)) {
            validationResult.setMessage("El email y dni " + baseMessage);
            validationResult.setStatusDni(true);
            validationResult.setStatus(true);
            validationResult.setIdUserByDni(this.userRepository.findByDni(dni).get().getId());
        } else if (dni!=null && this.userRepository.existsByDni(dni)) {
            validationResult.setMessage("El dni " + baseMessage);
            validationResult.setStatusDni(true);
            validationResult.setStatus(true);
            validationResult.setIdUserByDni(this.userRepository.findByDni(dni).get().getId());
        }

        return validationResult;
    }
    public void updateUserDataInSelfUpdate(User user, UserSelfUpdateDTO dto) {
        if (ValidationUtils.isValidAndChangedString(dto.getDni(), user.getDni())) {
            user.setDni(dto.getDni());
        }
        if (ValidationUtils.isValidAndChangedString(dto.getName(), user.getName())) {
            user.setName(dto.getName());
        }
        if (ValidationUtils.isValidAndChangedString(dto.getLastName(), user.getLastName())) {
            user.setLastName(dto.getLastName());
        }
        if (ValidationUtils.isValidAndChangedString(dto.getEmail(), user.getEmail())) {
            String fullName = user.getName() + " "+ user.getLastName();
            this.emailService.oldAccountEmail(user.getEmail(),dto.getEmail(),fullName);
            this.emailService.modifiedAccountEmail(user.getEmail(),dto.getEmail(),fullName,"");
            user.setEmail(dto.getEmail());
        }
    }

    public void validateDataInSelfUpdate(Long id, UserSelfUpdateDTO userSelfUpdateDTO){
        if (!this.userRepository.existsById(id)){
            throw new NotFoundException("No existe ningún usuario con el id: "+id.toString());
        }
        User currentUser = this.userRepository.findById(id).get();

        UserExistenceDTO responseExistsUser = this.checkUserExistence(
                userSelfUpdateDTO.getEmail(),
                userSelfUpdateDTO.getDni()
        );
        Boolean existUserWithNewEmail = responseExistsUser.getStatusEmail()
                && !Objects.equals(responseExistsUser.getIdUserByEmail(), id);
        Boolean existUserWithNewDni =  responseExistsUser.getStatusDni()
                && !Objects.equals(responseExistsUser.getIdUserByDni(), id);

        if (existUserWithNewEmail || existUserWithNewDni) {
            throw new BadRequestException(responseExistsUser.getMessage());
        }
        this.validatePasswordsInSelfUpdate(currentUser,userSelfUpdateDTO);
    }

    public void validatePasswordsInSelfUpdate(User user, UserSelfUpdateDTO dto){
        boolean oldPasswordProvided =  dto.getOldPassword() != null && ! dto.getOldPassword().isBlank();
        boolean isNewPasswordProvided = ((dto.getPassword() != null && !dto.getPassword().isBlank())
                || (dto.getRepeatPassword() != null && !dto.getRepeatPassword().isBlank()));

        if ( (oldPasswordProvided && !this.passwordService.matchesPasswords(dto.getOldPassword(),user.getPassword()))
                || (isNewPasswordProvided && !Objects.equals(dto.getPassword(), dto.getRepeatPassword()))){
            throw new ConflictException("Las contraseñas proporcionadas no son validas");
        }
    }
    public void updateUserDataInUpdateByAdmin(User user, UserAdminUpdateDTO dto) {
        if (ValidationUtils.isValidAndChangedString(dto.getDni(), user.getDni())) {
            user.setDni(dto.getDni());
        }
        if (ValidationUtils.isValidAndChangedString(dto.getName(), user.getName())) {
            user.setName(dto.getName());
        }
        if (ValidationUtils.isValidAndChangedString(dto.getLastName(), user.getLastName())) {
            user.setLastName(dto.getLastName());
        }
        if (ValidationUtils.isValidAndChangedString(dto.getEmail(), user.getEmail())) {
            updateEmail(user, dto);
        } else if (dto.getResetPassword()) {
            regenerateAndUpdatePasswordInUpdateByAdmin(user);
        }
    }
    public void updateEmail(User user, UserAdminUpdateDTO dto){
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

    public void regenerateAndUpdatePasswordInUpdateByAdmin(User user){
        String fullName = user.getName() + " "+ user.getLastName();
        String newPassword = this.passwordService.generateStrongPassword();
        String newEncodedPassword = this.passwordService.encodePasswords(newPassword);
        this.emailService.regeneratedPasswordEmail(user.getEmail(),fullName,newPassword);
        user.setPassword(newEncodedPassword);
    }
    public void validateDataToUpdateInUpdateByAdmin(Long id,UserAdminUpdateDTO userAdminUpdateDTO){
        if (!this.userRepository.existsById(id)){
            throw new NotFoundException("No existe ningún usuario con el id: "+id.toString());
        }
        UserExistenceDTO responseExistsUser = this.checkUserExistence(
                userAdminUpdateDTO.getEmail(),
                userAdminUpdateDTO.getDni()
        );
        Boolean existUserWithEmail = responseExistsUser.getStatusEmail()
                && !Objects.equals(responseExistsUser.getIdUserByEmail(), id);
        Boolean existUserWithDni =  (Boolean) responseExistsUser.getStatusDni()
                && !Objects.equals((Long) responseExistsUser.getIdUserByDni(), id);

        if (existUserWithEmail || existUserWithDni) {
            throw new BadRequestException((String) responseExistsUser.getMessage());
        }
    }
}
