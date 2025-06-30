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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserValidatorServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private PasswordService passwordService;
    @InjectMocks
    private UserValidatorService userValidatorService;
    private static final Long USER_ID = 2L;

    private static final String EXAMPLE_NAME = "example";
    private static final String EXAMPLE_LAST_NAME = "last name example";
    private static final String EXAMPLE_EMAIL = "test@example.com";
    private static final String EXAMPLE_PASS = "pass123";
    private static final String EXAMPLE_ENCODED_PASS = "encodedPass";
    private static final String EXAMPLE_DNI = "12345678A";
    private static final String EXAMPLE_OTHER_NAME = "Other Name";
    private static final String EXAMPLE_OTHER_EMAIL = "other@example.com";
    private static final String EXAMPLE_OTHER_LAST_NAME = "Other Last Name";
    private static final String EXAMPLE_OTHER_DNI = "23345452F";

    private static final User USER = new User(
            USER_ID,
            EXAMPLE_NAME,
            EXAMPLE_DNI,
            EXAMPLE_EMAIL,
            EXAMPLE_LAST_NAME
    );

    private static final User OTHER_USER = new User(
            55L,
            EXAMPLE_OTHER_NAME,
            EXAMPLE_OTHER_DNI,
            EXAMPLE_OTHER_EMAIL,
            EXAMPLE_OTHER_LAST_NAME
    );
    private static final String OLD_PASS = "old pass";
    private static final UserSelfUpdateDTO USER_SELF_UPDATE_DTO = new UserSelfUpdateDTO(
            EXAMPLE_OTHER_DNI,
            EXAMPLE_OTHER_EMAIL,
            OLD_PASS,
            EXAMPLE_PASS,
            EXAMPLE_PASS,
            EXAMPLE_OTHER_NAME,
            EXAMPLE_OTHER_LAST_NAME
    );

    @Test
    void checkUserExistenceNotExistsUser(){
        when(this.userRepository.existsByDni(EXAMPLE_DNI)).thenReturn(false);
        when(this.userRepository.existsByEmail(EXAMPLE_EMAIL)).thenReturn(false);

        UserExistenceDTO validationResult = this.userValidatorService.checkUserExistence(EXAMPLE_EMAIL,EXAMPLE_DNI);

        assertFalse(validationResult.getStatus());
    }

    @Test
    void checkUserExistenceExistsUserWithDni(){
        when(this.userRepository.existsByDni(EXAMPLE_DNI)).thenReturn(true);
        when(this.userRepository.findByDni(EXAMPLE_DNI)).thenReturn(Optional.of(USER));
        when(this.userRepository.existsByEmail(EXAMPLE_EMAIL)).thenReturn(false);

        UserExistenceDTO validationResult = this.userValidatorService.checkUserExistence(EXAMPLE_EMAIL,EXAMPLE_DNI);

        assertTrue(validationResult.getStatus());
        assertFalse(validationResult.getStatusEmail());
        assertTrue(validationResult.getStatusDni());
    }
    @Test
    void checkUserExistenceExistsUserWithEmail(){
        when(this.userRepository.existsByDni(EXAMPLE_DNI)).thenReturn(false);
        when(this.userRepository.findByEmail(EXAMPLE_EMAIL)).thenReturn(Optional.of(USER));
        when(this.userRepository.existsByEmail(EXAMPLE_EMAIL)).thenReturn(true);

        UserExistenceDTO validationResult = this.userValidatorService.checkUserExistence(EXAMPLE_EMAIL,EXAMPLE_DNI);

        assertTrue(validationResult.getStatus());
        assertTrue(validationResult.getStatusEmail());
        assertFalse(validationResult.getStatusDni());
    }

    @Test
    void checkUserExistenceExistsUserWithEmailAndDni(){
        when(this.userRepository.existsByDni(EXAMPLE_DNI)).thenReturn(true);
        when(this.userRepository.existsByEmail(EXAMPLE_EMAIL)).thenReturn(true);
        when(this.userRepository.findByDni(EXAMPLE_DNI)).thenReturn(Optional.of(USER));
        when(this.userRepository.findByEmail(EXAMPLE_EMAIL)).thenReturn(Optional.of(USER));


        UserExistenceDTO validationResult = this.userValidatorService.checkUserExistence(EXAMPLE_EMAIL,EXAMPLE_DNI);
        assertTrue(validationResult.getStatus());
        assertTrue(validationResult.getStatusEmail());
        assertTrue(validationResult.getStatusDni());
    }

    @Test
    void updateUserDataInSelfUpdateSuccessful(){
        this.userValidatorService.updateUserDataInSelfUpdate(USER,USER_SELF_UPDATE_DTO);
    }

    @Test
    void validateDataInSelfUpdateSuccessful(){
        when(this.userRepository.existsById(USER_ID)).thenReturn(true);
        when(this.userRepository.existsByDni(EXAMPLE_OTHER_DNI)).thenReturn(false);
        when(this.userRepository.existsByEmail(EXAMPLE_OTHER_EMAIL)).thenReturn(false);
        when(this.userRepository.findById(USER_ID)).thenReturn(Optional.of(USER));
        when(this.passwordService.matchesPasswords(any(String.class),any(String.class))).thenReturn(true);

        this.userValidatorService.validateDataInSelfUpdate(USER_ID,USER_SELF_UPDATE_DTO);
    }

    @Test
    void validateDataInSelfUpdateWhenExistsOtherUserWithEmailThrowBadRequestException(){
        when(this.userRepository.existsById(USER_ID)).thenReturn(true);
        when(this.userRepository.existsByDni(EXAMPLE_OTHER_DNI)).thenReturn(false);
        when(this.userRepository.existsByEmail(EXAMPLE_OTHER_EMAIL)).thenReturn(true);
        when(this.userRepository.findByEmail(EXAMPLE_OTHER_EMAIL)).thenReturn(Optional.of(OTHER_USER));
        when(this.userRepository.findById(USER_ID)).thenReturn(Optional.of(OTHER_USER));

        assertThrows(BadRequestException.class, () -> {
            this.userValidatorService.validateDataInSelfUpdate(USER_ID,USER_SELF_UPDATE_DTO);
        });
    }
    @Test
    void validateDataInSelfUpdateWhenExistsOtherUserWithDniThrowBadRequestException(){
        when(this.userRepository.existsById(USER_ID)).thenReturn(true);
        when(this.userRepository.existsByDni(EXAMPLE_OTHER_DNI)).thenReturn(true);
        when(this.userRepository.existsByEmail(EXAMPLE_OTHER_EMAIL)).thenReturn(false);
        when(this.userRepository.findByDni(EXAMPLE_OTHER_DNI)).thenReturn(Optional.of(OTHER_USER));
        when(this.userRepository.findById(USER_ID)).thenReturn(Optional.of(OTHER_USER));

        assertThrows(BadRequestException.class, () -> {
            this.userValidatorService.validateDataInSelfUpdate(USER_ID,USER_SELF_UPDATE_DTO);
        });
    }
    @Test
    void validateDataInSelfUpdateWhenNotExistsUserIdThrowNotFoundException(){
        when(this.userRepository.existsById(USER_ID)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> {
            this.userValidatorService.validateDataInSelfUpdate(USER_ID,USER_SELF_UPDATE_DTO);
        });
    }
    @Test
    void validatePasswordsInSelfUpdateSuccessful(){
        User userWithPass = new User(
                55L,
                EXAMPLE_OTHER_NAME,
                EXAMPLE_OTHER_DNI,
                EXAMPLE_OTHER_EMAIL,
                EXAMPLE_OTHER_LAST_NAME
        );
        userWithPass.setPassword(EXAMPLE_ENCODED_PASS);
        when(this.passwordService.matchesPasswords(any(String.class),any(String.class))).thenReturn(true);
        this.userValidatorService.validatePasswordsInSelfUpdate(userWithPass,USER_SELF_UPDATE_DTO);
    }

    @Test
    void validatePasswordsInSelfUpdateOldPasswordNotMatchesThrowConflictRequestException(){
        User userWithPass = new User(
                55L,
                EXAMPLE_OTHER_NAME,
                EXAMPLE_OTHER_DNI,
                EXAMPLE_OTHER_EMAIL,
                EXAMPLE_OTHER_LAST_NAME
        );
        UserSelfUpdateDTO otherSelfUpdate = new UserSelfUpdateDTO(
                EXAMPLE_OTHER_DNI,
                EXAMPLE_OTHER_EMAIL,
                OLD_PASS,
                EXAMPLE_PASS,
                EXAMPLE_PASS,
                EXAMPLE_OTHER_NAME,
                EXAMPLE_OTHER_LAST_NAME
        );
        userWithPass.setPassword(EXAMPLE_ENCODED_PASS);
        when(this.passwordService.matchesPasswords(any(String.class),any(String.class))).thenReturn(false);
        assertThrows(ConflictException.class, () -> {
            this.userValidatorService.validatePasswordsInSelfUpdate(userWithPass,otherSelfUpdate);
        });
    }
    @Test
    void validatePasswordsInSelfUpdateOldPasswordNullThrowConflictRequestException(){
        User userWithPass = new User(
                55L,
                EXAMPLE_OTHER_NAME,
                EXAMPLE_OTHER_DNI,
                EXAMPLE_OTHER_EMAIL,
                EXAMPLE_OTHER_LAST_NAME
        );
        UserSelfUpdateDTO otherSelfUpdate = new UserSelfUpdateDTO(
                EXAMPLE_OTHER_DNI,
                EXAMPLE_OTHER_EMAIL,
                null,
                EXAMPLE_PASS,
                EXAMPLE_PASS,
                EXAMPLE_OTHER_NAME,
                EXAMPLE_OTHER_LAST_NAME
        );
        userWithPass.setPassword(EXAMPLE_ENCODED_PASS);
        assertThrows(ConflictException.class, () -> {
            this.userValidatorService.validatePasswordsInSelfUpdate(userWithPass,otherSelfUpdate);
        });
    }
    @Test
    void validatePasswordsInSelfUpdateOldPasswordBlankThrowConflictRequestException(){
        User userWithPass = new User(
                55L,
                EXAMPLE_OTHER_NAME,
                EXAMPLE_OTHER_DNI,
                EXAMPLE_OTHER_EMAIL,
                EXAMPLE_OTHER_LAST_NAME
        );
        UserSelfUpdateDTO otherSelfUpdate = new UserSelfUpdateDTO(
                EXAMPLE_OTHER_DNI,
                EXAMPLE_OTHER_EMAIL,
                "",
                EXAMPLE_PASS,
                EXAMPLE_PASS,
                EXAMPLE_OTHER_NAME,
                EXAMPLE_OTHER_LAST_NAME
        );
        userWithPass.setPassword(EXAMPLE_ENCODED_PASS);
        assertThrows(ConflictException.class, () -> {
            this.userValidatorService.validatePasswordsInSelfUpdate(userWithPass,otherSelfUpdate);
        });
    }
    @Test
    void validatePasswordsInSelfUpdateRepeatPasswordsNullThrowConflictRequestException(){
        User userWithPass = new User(
                55L,
                EXAMPLE_OTHER_NAME,
                EXAMPLE_OTHER_DNI,
                EXAMPLE_OTHER_EMAIL,
                EXAMPLE_OTHER_LAST_NAME
        );
        UserSelfUpdateDTO otherSelfUpdate = new UserSelfUpdateDTO(
                EXAMPLE_OTHER_DNI,
                EXAMPLE_OTHER_EMAIL,
                OLD_PASS,
                EXAMPLE_PASS,
                null,
                EXAMPLE_OTHER_NAME,
                EXAMPLE_OTHER_LAST_NAME
        );
        userWithPass.setPassword(EXAMPLE_ENCODED_PASS);
        when(this.passwordService.matchesPasswords(any(String.class),any(String.class))).thenReturn(false);
        assertThrows(ConflictException.class, () -> {
            this.userValidatorService.validatePasswordsInSelfUpdate(userWithPass,otherSelfUpdate);
        });
    }

    @Test
    void validatePasswordsInSelfUpdateRepeatPasswordsBlankThrowConflictRequestException(){
        User userWithPass = new User(
                55L,
                EXAMPLE_OTHER_NAME,
                EXAMPLE_OTHER_DNI,
                EXAMPLE_OTHER_EMAIL,
                EXAMPLE_OTHER_LAST_NAME
        );
        UserSelfUpdateDTO otherSelfUpdate = new UserSelfUpdateDTO(
                EXAMPLE_OTHER_DNI,
                EXAMPLE_OTHER_EMAIL,
                OLD_PASS,
                EXAMPLE_PASS,
                "",
                EXAMPLE_OTHER_NAME,
                EXAMPLE_OTHER_LAST_NAME
        );
        userWithPass.setPassword(EXAMPLE_ENCODED_PASS);
        when(this.passwordService.matchesPasswords(any(String.class),any(String.class))).thenReturn(false);
        assertThrows(ConflictException.class, () -> {
            this.userValidatorService.validatePasswordsInSelfUpdate(userWithPass,otherSelfUpdate);
        });
    }

    @Test
    void validatePasswordsInSelfUpdatePasswordsBlankThrowConflictRequestException(){
        User userWithPass = new User(
                55L,
                EXAMPLE_OTHER_NAME,
                EXAMPLE_OTHER_DNI,
                EXAMPLE_OTHER_EMAIL,
                EXAMPLE_OTHER_LAST_NAME
        );
        UserSelfUpdateDTO otherSelfUpdate = new UserSelfUpdateDTO(
                EXAMPLE_OTHER_DNI,
                EXAMPLE_OTHER_EMAIL,
                OLD_PASS,
                "",
                EXAMPLE_PASS,
                EXAMPLE_OTHER_NAME,
                EXAMPLE_OTHER_LAST_NAME
        );
        userWithPass.setPassword(EXAMPLE_ENCODED_PASS);
        when(this.passwordService.matchesPasswords(any(String.class),any(String.class))).thenReturn(false);
        assertThrows(ConflictException.class, () -> {
            this.userValidatorService.validatePasswordsInSelfUpdate(userWithPass,otherSelfUpdate);
        });
    }

    @Test
    void validatePasswordsInSelfUpdatePasswordNullThrowConflictRequestException(){
        User userWithPass = new User(
                55L,
                EXAMPLE_OTHER_NAME,
                EXAMPLE_OTHER_DNI,
                EXAMPLE_OTHER_EMAIL,
                EXAMPLE_OTHER_LAST_NAME
        );
        UserSelfUpdateDTO otherSelfUpdate = new UserSelfUpdateDTO(
                EXAMPLE_OTHER_DNI,
                EXAMPLE_OTHER_EMAIL,
                OLD_PASS,
                null,
                EXAMPLE_PASS,
                EXAMPLE_OTHER_NAME,
                EXAMPLE_OTHER_LAST_NAME
        );
        userWithPass.setPassword(EXAMPLE_ENCODED_PASS);
        when(this.passwordService.matchesPasswords(any(String.class),any(String.class))).thenReturn(false);
        assertThrows(ConflictException.class, () -> {
            this.userValidatorService.validatePasswordsInSelfUpdate(userWithPass,otherSelfUpdate);
        });
    }
    @Test
    void updateUserDataInUpdateByAdminUpdateEmailSuccessful(){
        UserAdminUpdateDTO userAdminUpdateDTO = new UserAdminUpdateDTO(
                EXAMPLE_OTHER_DNI,EXAMPLE_OTHER_EMAIL,true,EXAMPLE_OTHER_NAME,EXAMPLE_OTHER_LAST_NAME
        );
        when(this.passwordService.generateStrongPassword()).thenReturn(EXAMPLE_PASS);
        when(this.passwordService.encodePasswords(EXAMPLE_PASS)).thenReturn(EXAMPLE_ENCODED_PASS);
        this.userValidatorService.updateUserDataInUpdateByAdmin(USER,userAdminUpdateDTO);
    }

    @Test
    void updateUserDataInUpdateByAdminResetPasswordSuccessful(){
        UserAdminUpdateDTO userAdminUpdateDTO = new UserAdminUpdateDTO(
                EXAMPLE_OTHER_DNI,EXAMPLE_EMAIL,true,EXAMPLE_OTHER_NAME,EXAMPLE_OTHER_LAST_NAME
        );
        when(this.passwordService.generateStrongPassword()).thenReturn(EXAMPLE_PASS);
        when(this.passwordService.encodePasswords(EXAMPLE_PASS)).thenReturn(EXAMPLE_ENCODED_PASS);
        this.userValidatorService.updateUserDataInUpdateByAdmin(USER,userAdminUpdateDTO);
    }
    @Test
    void validateDataToUpdateInUpdateByAdminSuccessful(){
        UserAdminUpdateDTO userAdminUpdateDTO = new UserAdminUpdateDTO(
                EXAMPLE_OTHER_DNI,EXAMPLE_EMAIL,true,EXAMPLE_OTHER_NAME,EXAMPLE_OTHER_LAST_NAME
        );
        when(this.userRepository.existsById(USER_ID)).thenReturn(true);
        when(this.userRepository.existsByEmail(EXAMPLE_EMAIL)).thenReturn(false);
        when(this.userRepository.existsByDni(EXAMPLE_OTHER_DNI)).thenReturn(false);

        this.userValidatorService.validateDataToUpdateInUpdateByAdmin(USER_ID,userAdminUpdateDTO);
    }
    @Test
    void validateDataToUpdateInUpdateByAdminWhenUserNotExistsThrowNotFoundException(){
        UserAdminUpdateDTO userAdminUpdateDTO = new UserAdminUpdateDTO(
                EXAMPLE_OTHER_DNI,EXAMPLE_EMAIL,true,EXAMPLE_OTHER_NAME,EXAMPLE_OTHER_LAST_NAME
        );
        when(this.userRepository.existsById(USER_ID)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> {
            this.userValidatorService.validateDataToUpdateInUpdateByAdmin(USER_ID,userAdminUpdateDTO);
        });
    }

    @Test
    void validateDataToUpdateInUpdateByAdminExistsUserWithOtherEmailThrowBadRequestException(){
        UserAdminUpdateDTO userAdminUpdateDTO = new UserAdminUpdateDTO(
                EXAMPLE_OTHER_DNI,EXAMPLE_EMAIL,true,EXAMPLE_OTHER_NAME,EXAMPLE_OTHER_LAST_NAME
        );
        User userWithEmail = new User(77L, EXAMPLE_OTHER_NAME, EXAMPLE_OTHER_DNI,
                EXAMPLE_OTHER_EMAIL,EXAMPLE_OTHER_LAST_NAME);
        when(this.userRepository.existsById(USER_ID)).thenReturn(true);
        when(this.userRepository.existsByEmail(EXAMPLE_EMAIL)).thenReturn(true);
        when(this.userRepository.findByEmail(EXAMPLE_EMAIL)).thenReturn(Optional.of(userWithEmail));
        when(this.userRepository.existsByDni(EXAMPLE_OTHER_DNI)).thenReturn(false);

        assertThrows(BadRequestException.class, () -> {
            this.userValidatorService.validateDataToUpdateInUpdateByAdmin(USER_ID,userAdminUpdateDTO);
        });
    }
    @Test
    void validateDataToUpdateInUpdateByAdminExistsUserWithOtherDniThrowBadRequestException(){
        UserAdminUpdateDTO userAdminUpdateDTO = new UserAdminUpdateDTO(
                EXAMPLE_OTHER_DNI,EXAMPLE_EMAIL,true,EXAMPLE_OTHER_NAME,EXAMPLE_OTHER_LAST_NAME
        );
        User userWithDni = new User(77L, EXAMPLE_OTHER_NAME, EXAMPLE_OTHER_DNI,
                EXAMPLE_OTHER_EMAIL,EXAMPLE_OTHER_LAST_NAME);
        when(this.userRepository.existsById(USER_ID)).thenReturn(true);
        when(this.userRepository.existsByEmail(EXAMPLE_EMAIL)).thenReturn(false);
        when(this.userRepository.findByDni(EXAMPLE_OTHER_DNI)).thenReturn(Optional.of(userWithDni));
        when(this.userRepository.existsByDni(EXAMPLE_OTHER_DNI)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> {
            this.userValidatorService.validateDataToUpdateInUpdateByAdmin(USER_ID,userAdminUpdateDTO);
        });
    }
}
