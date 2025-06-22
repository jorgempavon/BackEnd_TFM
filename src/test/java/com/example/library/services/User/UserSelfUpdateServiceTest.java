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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserSelfUpdateServiceTest {
    @Mock
    private PasswordService passwordService;
    @Mock
    private EmailService emailService;
    @Mock
    private UserValidatorService userValidatorService;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserSelfUpdateService userSelfUpdateService;

    private static final Long exampleId = 2L;
    private static final String exampleName = "example";
    private static final String exampleLastName = "last name example";
    private static final String exampleEmail = "test@example.com";
    private static final String examplePass = "pass123";
    private static final String exampleOtherPass = "OtherPass123";
    private static final String exampleEncodedPass = "encodedPass";
    private static final String exampleDni = "12345678A";
    private static final String exampleOtherEmail = "other@example.com";
    private static final String exampleOtherName = "Other Name";
    private static final String exampleOtherLastName = "Other Last Name";
    private static final String exampleOtherDni = "23345452F";
    private final String status = "status";
    private final String message = "message";

    private final String statusEmail = "statusEmail";
    private final String statusDni = "statusDni";

    @Test
    void updateSelfUpdateDTO_ChangeUserRolToClient_successful(){
        UserSelfUpdateDTO userSelfUpdateDTO = new UserSelfUpdateDTO(exampleDni,exampleEmail,exampleOtherPass,
                examplePass,examplePass,
                exampleName,exampleLastName);
        User currentUser = new User(exampleOtherName,exampleOtherDni,exampleOtherEmail,exampleOtherLastName);
        currentUser.setPassword(exampleEncodedPass);

        Map<String, Object> responseExistsUser = new HashMap<>();
        responseExistsUser.put(status,false);
        responseExistsUser.put(statusEmail,false);
        responseExistsUser.put(statusDni,false);
        responseExistsUser.put(message,"");

        when(this.userValidatorService.checkUserExistence(exampleEmail,exampleDni)).thenReturn(responseExistsUser);
        when(this.userRepository.existsById(exampleId)).thenReturn(true);
        when(this.userRepository.findById(exampleId)).thenReturn(Optional.of(currentUser));

        when(this.userRepository.existsByEmail(exampleEmail)).thenReturn(false);
        when(this.userRepository.existsByDni(exampleDni)).thenReturn(false);
        when(this.passwordService.matchesPasswords(exampleOtherPass,exampleEncodedPass)).thenReturn(true);
        when(this.passwordService.encodePasswords(examplePass)).thenReturn(exampleEncodedPass);

        doNothing()
                .when(emailService)
                .oldAccountEmail(any(String.class), any(String.class), any(String.class));

        doNothing()
                .when(emailService)
                .modifiedAccountEmail(any(String.class), any(String.class), any(String.class), any(String.class));

        UserDTO responseUserDto = this.userSelfUpdateService.update(exampleId,userSelfUpdateDTO);

        assertEquals(responseUserDto.getName(),exampleName);
        assertEquals(responseUserDto.getEmail(),exampleEmail);
        assertEquals(responseUserDto.getLastName(),exampleLastName);
    }
    @Test
    void updateSelfUpdateDTO_whenNotExistsUser_throwsNotFoundException(){
        UserSelfUpdateDTO userSelfUpdateDTO = new UserSelfUpdateDTO(exampleDni,exampleEmail,exampleOtherPass,
                examplePass,examplePass,
                exampleName,exampleLastName);

        when(this.userRepository.existsById(exampleId)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> {
            this.userSelfUpdateService.update(exampleId,userSelfUpdateDTO);
        });

    }
    @Test
    void updateSelfUpdateDTO_whenExistsOtherUserWithEmail_throwsBadRequestException(){
        UserSelfUpdateDTO userSelfUpdateDTO = new UserSelfUpdateDTO(exampleDni,exampleEmail,exampleOtherPass,
                examplePass,examplePass,
                exampleName,exampleLastName);
        User currentUser = new User(exampleOtherName,exampleOtherDni,exampleOtherEmail,exampleOtherLastName);
        currentUser.setPassword(exampleEncodedPass);
        currentUser.setId(0L);

        Map<String, Object> responseExistsUser = new HashMap<>();
        responseExistsUser.put(status,true);
        responseExistsUser.put(statusEmail,true);
        responseExistsUser.put(statusDni,false);
        responseExistsUser.put(message,"");

        when(this.userValidatorService.checkUserExistence(exampleEmail,exampleDni)).thenReturn(responseExistsUser);

        when(this.userRepository.existsById(exampleId)).thenReturn(true);
        when(this.userRepository.findById(exampleId)).thenReturn(Optional.of(currentUser));

        when(this.userRepository.existsByEmail(exampleEmail)).thenReturn(true);
        when(this.userRepository.existsByDni(exampleDni)).thenReturn(false);
        when(this.userRepository.findByEmail(exampleEmail)).thenReturn(Optional.of(currentUser));

        assertThrows(BadRequestException.class, () -> {
            this.userSelfUpdateService.update(exampleId,userSelfUpdateDTO);
        });
    }

    @Test
    void updateSelfUpdateDTO_whenExistsOtherUserWithDni_throwsBadRequestException(){
        UserSelfUpdateDTO userSelfUpdateDTO = new UserSelfUpdateDTO(exampleDni,exampleEmail,exampleOtherPass,
                examplePass,examplePass,
                exampleName,exampleLastName);
        User currentUser = new User(exampleOtherName,exampleOtherDni,exampleOtherEmail,exampleOtherLastName);
        currentUser.setPassword(exampleEncodedPass);
        currentUser.setId(0L);

        Map<String, Object> responseExistsUser = new HashMap<>();
        responseExistsUser.put(status,true);
        responseExistsUser.put(statusEmail,false);
        responseExistsUser.put(statusDni,true);
        responseExistsUser.put(message,"");

        when(this.userValidatorService.checkUserExistence(exampleEmail,exampleDni)).thenReturn(responseExistsUser);

        when(this.userRepository.existsById(exampleId)).thenReturn(true);
        when(this.userRepository.findById(exampleId)).thenReturn(Optional.of(currentUser));

        when(this.userRepository.existsByEmail(exampleEmail)).thenReturn(false);
        when(this.userRepository.existsByDni(exampleDni)).thenReturn(true);
        when(this.userRepository.findByDni(exampleDni)).thenReturn(Optional.of(currentUser));

        assertThrows(BadRequestException.class, () -> {
            this.userSelfUpdateService.update(exampleId,userSelfUpdateDTO);
        });
    }

    @Test
    void updateSelfUpdateDTO_whenOldPasswordIsWrong_throwsConflictException(){
        UserSelfUpdateDTO userSelfUpdateDTO = new UserSelfUpdateDTO(exampleDni,exampleEmail,exampleOtherPass,
                examplePass,examplePass,
                exampleName,exampleLastName);
        User currentUser = new User(exampleOtherName,exampleOtherDni,exampleOtherEmail,exampleOtherLastName);
        currentUser.setPassword(exampleEncodedPass);
        currentUser.setId(0L);

        Map<String, Object> responseExistsUser = new HashMap<>();
        responseExistsUser.put(status,false);
        responseExistsUser.put(statusEmail,false);
        responseExistsUser.put(statusDni,false);
        responseExistsUser.put(message,"");

        when(this.userValidatorService.checkUserExistence(exampleEmail,exampleDni)).thenReturn(responseExistsUser);

        when(this.userRepository.existsById(exampleId)).thenReturn(true);
        when(this.userRepository.findById(exampleId)).thenReturn(Optional.of(currentUser));

        when(this.userRepository.existsByEmail(exampleEmail)).thenReturn(false);
        when(this.userRepository.existsByDni(exampleDni)).thenReturn(false);
        when(this.passwordService.matchesPasswords(any(String.class),any(String.class))).thenReturn(false);

        assertThrows(ConflictException.class, () -> {
            this.userSelfUpdateService.update(exampleId,userSelfUpdateDTO);
        });
    }

    @Test
    void updateSelfUpdateDTO_whenNewPasswordsNotMatches_throwsConflictException(){
        UserSelfUpdateDTO userSelfUpdateDTO = new UserSelfUpdateDTO(exampleDni,exampleEmail,exampleOtherPass,
                examplePass,"badPass",
                exampleName,exampleLastName);
        User currentUser = new User(exampleOtherName,exampleOtherDni,exampleOtherEmail,exampleOtherLastName);
        currentUser.setPassword(exampleEncodedPass);
        currentUser.setId(0L);

        Map<String, Object> responseExistsUser = new HashMap<>();
        responseExistsUser.put(status,false);
        responseExistsUser.put(statusEmail,false);
        responseExistsUser.put(statusDni,false);
        responseExistsUser.put(message,"");

        when(this.userValidatorService.checkUserExistence(exampleEmail,exampleDni)).thenReturn(responseExistsUser);

        when(this.userRepository.existsById(exampleId)).thenReturn(true);
        when(this.userRepository.findById(exampleId)).thenReturn(Optional.of(currentUser));

        when(this.userRepository.existsByEmail(exampleEmail)).thenReturn(false);
        when(this.userRepository.existsByDni(exampleDni)).thenReturn(false);
        when(this.passwordService.matchesPasswords(any(String.class),any(String.class))).thenReturn(true);

        assertThrows(ConflictException.class, () -> {
            this.userSelfUpdateService.update(exampleId,userSelfUpdateDTO);
        });
    }

}
