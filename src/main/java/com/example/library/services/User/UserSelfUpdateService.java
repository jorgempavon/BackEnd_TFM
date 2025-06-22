package com.example.library.services.User;


import com.example.library.api.exceptions.models.BadRequestException;
import com.example.library.api.exceptions.models.ConflictException;
import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.config.PasswordService;
import com.example.library.entities.dto.UserDTO;
import com.example.library.entities.dto.UserSelfUpdateDTO;
import com.example.library.entities.model.User;
import com.example.library.entities.repository.UserRepository;
import com.example.library.services.EmailService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
public class UserSelfUpdateService {
    private final PasswordService passwordService;
    private final EmailService emailService;
    private final UserValidatorService userValidatorService;
    private final UserRepository userRepository;

    public UserSelfUpdateService(UserValidatorService userValidatorService,
                                 PasswordService passwordService,
                                 EmailService emailService,
                                 UserRepository userRepository){
        this.emailService = emailService;
        this.userValidatorService = userValidatorService;
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }
    @Transactional
    public UserDTO update(Long id, UserSelfUpdateDTO userSelfUpdateDTO){
        validateDataToUpdate(id, userSelfUpdateDTO);

        User user = this.userRepository.findById(id).get();
        updateUserData(user,userSelfUpdateDTO);

        if (userSelfUpdateDTO.getPassword() != null && !userSelfUpdateDTO.getPassword().isBlank()) {
            String encodedPassword = this.passwordService.encodePasswords(userSelfUpdateDTO.getPassword());
            user.setPassword(encodedPassword);
        }
        this.userRepository.save(user);
        return new UserDTO(user.getId(),user.getName(),user.getEmail(),user.getDni(),user.getLastName(),user.getRol());
    }

    private void updateUserData(User user, UserSelfUpdateDTO dto) {
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
            String fullName = user.getName() + " "+ user.getLastName();
            this.emailService.oldAccountEmail(user.getEmail(),dto.getEmail(),fullName);
            this.emailService.modifiedAccountEmail(user.getEmail(),dto.getEmail(),fullName,"");
            user.setEmail(dto.getEmail());
        }
    }

    private void validateDataToUpdate(Long id, UserSelfUpdateDTO userSelfUpdateDTO){
        if (!this.userRepository.existsById(id)){
            throw new NotFoundException("No existe ningún usuario con el id: "+id.toString());
        }
        User currentUser = this.userRepository.findById(id).get();

        Map<String, Object> responseExistsUser = this.userValidatorService.checkUserExistence(
                userSelfUpdateDTO.getEmail(),
                userSelfUpdateDTO.getDni()
        );
        String statusEmail = "statusEmail";
        String idUserByEmail = "idUserByEmail";
        String idUserByDni = "idUserByDni";
        Boolean existUserWithNewEmail = (Boolean) responseExistsUser.get(statusEmail)
                && !Objects.equals((Long) responseExistsUser.get(idUserByEmail), id);
        String statusDni = "statusDni";
        Boolean existUserWithNewDni =  (Boolean) responseExistsUser.get(statusDni)
                && !Objects.equals((Long) responseExistsUser.get(idUserByDni), id);

        if (existUserWithNewEmail || existUserWithNewDni) {
            String message = "message";
            throw new BadRequestException((String) responseExistsUser.get(message));
        }
        this.validatePasswordsInSelfUpdate(currentUser,userSelfUpdateDTO);
    }

    private void validatePasswordsInSelfUpdate(User user, UserSelfUpdateDTO dto){
        boolean oldPasswordProvided =  dto.getOldPassword() != null && ! dto.getOldPassword().isBlank();
        boolean isNewPasswordProvided = ((dto.getPassword() != null && !dto.getPassword().isBlank())
                || (dto.getRepeatPassword() != null && !dto.getRepeatPassword().isBlank()));

        if ( (oldPasswordProvided && !this.passwordService.matchesPasswords(dto.getOldPassword(),user.getPassword()))
                || (isNewPasswordProvided && !Objects.equals(dto.getPassword(), dto.getRepeatPassword()))){
            throw new ConflictException("Las contraseñas proporcionadas no son validas");
        }
    }
}
