package com.example.library.services.User;

import com.example.library.config.PasswordService;
import com.example.library.entities.dto.UserCreateDTO;
import com.example.library.entities.dto.UserSaveDTO;
import com.example.library.entities.model.User;
import com.example.library.entities.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserValidatorServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordService passwordService;
    @InjectMocks
    private UserValidatorService userValidatorService;
    private final String status = "status";
    private final String message = "message";
    private final String statusEmail = "statusEmail";
    private final String statusDni = "statusDni";
    private static final String rol = "client";
    private static final String exampleName = "example";
    private static final String exampleLastName = "last name example";
    private static final String exampleEmail = "test@example.com";
    private static final String examplePass = "pass123";
    private static final String exampleEncodedPass = "encodedPass";
    private static final String exampleDni = "12345678A";
    private static final String exampleOtherName = "Other Name";

    private static final User user = new User(
            exampleName,
            exampleDni,
            exampleEmail,
            exampleLastName
    );
    private static final UserCreateDTO userCreateDTO = new UserCreateDTO(
            exampleDni,
            exampleEmail,
            exampleName,
            exampleLastName
    );

    @Test
    void checkUserExistence_NotExistsUser(){
        when(this.userRepository.existsByDni(exampleDni)).thenReturn(false);
        when(this.userRepository.existsByEmail(exampleEmail)).thenReturn(false);

        Map<String, Object> validationResult = this.userValidatorService.checkUserExistence(exampleEmail,exampleDni);

        assertFalse((Boolean) validationResult.get(status));
    }

    @Test
    void checkUserExistence_ExistsUserWithDni(){
        when(this.userRepository.existsByDni(exampleDni)).thenReturn(true);
        when(this.userRepository.findByDni(exampleDni)).thenReturn(Optional.of(user));
        when(this.userRepository.existsByEmail(exampleEmail)).thenReturn(false);

        Map<String, Object> validationResult = this.userValidatorService.checkUserExistence(exampleEmail,exampleDni);

        assertTrue((Boolean) validationResult.get(status));
        assertFalse((Boolean) validationResult.get(statusEmail));
        assertTrue((Boolean) validationResult.get(statusDni));
    }
    @Test
    void checkUserExistence_ExistsUserWithEmail(){
        when(this.userRepository.existsByDni(exampleDni)).thenReturn(false);
        when(this.userRepository.findByEmail(exampleEmail)).thenReturn(Optional.of(user));
        when(this.userRepository.existsByEmail(exampleEmail)).thenReturn(true);

        Map<String, Object> validationResult = this.userValidatorService.checkUserExistence(exampleEmail,exampleDni);

        assertTrue((Boolean) validationResult.get(status));
        assertTrue((Boolean) validationResult.get(statusEmail));
        assertFalse((Boolean) validationResult.get(statusDni));
    }

    @Test
    void checkUserExistence_ExistsUserWithEmailAndDni(){
        when(this.userRepository.existsByDni(exampleDni)).thenReturn(true);
        when(this.userRepository.findByDni(exampleDni)).thenReturn(Optional.of(user));
        when(this.userRepository.findByEmail(exampleEmail)).thenReturn(Optional.of(user));
        when(this.userRepository.existsByEmail(exampleEmail)).thenReturn(true);

        Map<String, Object> validationResult = this.userValidatorService.checkUserExistence(exampleEmail,exampleDni);

        assertTrue((Boolean) validationResult.get(status));
        assertTrue((Boolean) validationResult.get(statusEmail));
        assertTrue((Boolean) validationResult.get(statusDni));
    }

    @Test
    void isValidAndChanged_returnsTrue(){
        assertTrue(this.userValidatorService.isValidAndChanged(exampleName,exampleOtherName));
    }

    @Test
    void isValidAndChanged_returnsFalse_nullNewValue(){
        assertTrue(this.userValidatorService.isValidAndChanged(null,exampleOtherName));
    }

    @Test
    void isValidAndChanged_returnsFalse_BlankNewValue(){
        assertTrue(this.userValidatorService.isValidAndChanged("",exampleOtherName));
    }

    @Test
    void isValidAndChanged_returnsFalse_IsEqualsValues(){
        assertTrue(this.userValidatorService.isValidAndChanged(exampleOtherName,exampleOtherName));
    }
    @Test
    void buildUserSaveDto_successful(){
        when(this.passwordService.encodePasswords(examplePass)).thenReturn(exampleEncodedPass);
        UserSaveDTO reponseSaveDto = this.userValidatorService.buildUserSaveDto(userCreateDTO,exampleEncodedPass,rol);

        assertEquals(reponseSaveDto.getName(), exampleName);
        assertEquals(reponseSaveDto.getLastName(), exampleLastName);
        assertEquals(reponseSaveDto.getDni(), exampleDni);
        assertEquals(reponseSaveDto.getRol(), rol);
        assertEquals(reponseSaveDto.getEmail(), exampleEmail);
        assertEquals(reponseSaveDto.getPasswordEncoded(), exampleEncodedPass);
    }
}
