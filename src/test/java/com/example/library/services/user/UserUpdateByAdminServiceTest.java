package com.example.library.services.user;

import com.example.library.api.exceptions.models.BadRequestException;
import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.config.PasswordService;
import com.example.library.entities.dto.UserAdminUpdateDTO;
import com.example.library.entities.dto.UserDTO;
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
public class UserUpdateByAdminServiceTest {
    @Mock
    private PasswordService passwordService;
    @Mock
    private EmailService emailService;
    @Mock
    private UserValidatorService userValidatorService;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserUpdateByAdminService userUpdateByAdminService;

    private static final Long exampleId = 2L;
    private static final String exampleName = "example";
    private static final String exampleLastName = "last name example";
    private static final String exampleEmail = "test@example.com";
    private static final String examplePass = "pass123";
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
    void updateUserAdminUpdateDTO_successful(){
        UserAdminUpdateDTO userAdminUpdateDTO = new UserAdminUpdateDTO(exampleDni,exampleEmail,true,
                exampleName,exampleLastName);
        User currentUser = new User(exampleOtherName,exampleOtherDni,exampleOtherEmail,exampleOtherLastName);

        Map<String, Object> responseExistsUser = new HashMap<>();
        responseExistsUser.put(status,false);
        responseExistsUser.put(statusEmail,false);
        responseExistsUser.put(statusDni,false);
        responseExistsUser.put(message,"");

        when(this.userValidatorService.checkUserExistence(exampleEmail,exampleDni)).thenReturn(responseExistsUser);
        when(this.userValidatorService.isValidAndChanged(exampleName,exampleOtherName)).thenReturn(true);
        when(this.userValidatorService.isValidAndChanged(exampleLastName,exampleOtherLastName)).thenReturn(true);
        when(this.userValidatorService.isValidAndChanged(exampleEmail,exampleOtherEmail)).thenReturn(true);
        when(this.userValidatorService.isValidAndChanged(exampleDni,exampleOtherDni)).thenReturn(true);

        when(this.userRepository.existsById(exampleId)).thenReturn(true);
        when(this.userRepository.findById(exampleId)).thenReturn(Optional.of(currentUser));

        when(this.passwordService.generateStrongPassword()).thenReturn(examplePass);
        when(this.passwordService.encodePasswords(examplePass)).thenReturn(exampleEncodedPass);

        doNothing()
                .when(emailService)
                .modifiedAccountEmail(any(String.class), any(String.class), any(String.class),any(String.class));

        UserDTO responseUserDto = this.userUpdateByAdminService.update(exampleId,userAdminUpdateDTO);

        assertEquals(responseUserDto.getName(),exampleName);
        assertEquals(responseUserDto.getEmail(),exampleEmail);
        assertEquals(responseUserDto.getLastName(),exampleLastName);
    }

    @Test
    void updateAdminUpdateDTO_whenNotExistsUser_throwsNotFoundException(){
        UserAdminUpdateDTO userAdminUpdateDTO = new UserAdminUpdateDTO(exampleDni,null,true,
                exampleName,exampleLastName);

        when(this.userRepository.existsById(exampleId)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> {
            this.userUpdateByAdminService.update(exampleId,userAdminUpdateDTO);
        });

    }
    @Test
    void updateAdminUpdateDTO_whenExistsOtherUserWithEmail_throwsBadRequestException(){
        UserAdminUpdateDTO userAdminUpdateDTO = new UserAdminUpdateDTO(exampleDni,exampleEmail,true,
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

        assertThrows(BadRequestException.class, () -> {
            this.userUpdateByAdminService.update(exampleId,userAdminUpdateDTO);
        });
    }

    @Test
    void updateAdminUpdateDTO_whenExistsOtherUserWithDni_throwsBadRequestException(){
        UserAdminUpdateDTO userAdminUpdateDTO = new UserAdminUpdateDTO(exampleDni,exampleEmail,true,
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

        assertThrows(BadRequestException.class, () -> {
            this.userUpdateByAdminService.update(exampleId,userAdminUpdateDTO);
        });
    }
}
