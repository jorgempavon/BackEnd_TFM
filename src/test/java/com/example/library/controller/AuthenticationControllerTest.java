package com.example.library.controller;

import com.example.library.api.exceptions.models.ConflictException;
import com.example.library.api.exceptions.models.BadRequestException;
import com.example.library.entities.dto.*;
import com.example.library.entities.model.User;
import com.example.library.entities.repository.ClientRepository;
import com.example.library.entities.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;


import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationController authController;


    @Test
    void register_successful() {
        UserRegisterDTO newUserRegisterDto = new UserRegisterDTO();
        newUserRegisterDto.setEmail("test@example.com");
        newUserRegisterDto.setDni("12345678A");
        newUserRegisterDto.setPassword("pass123");
        newUserRegisterDto.setRepeatPassword("pass123");

        String encodedPassword = "hashedPass";
        User user = new User();
        user.setPassword(encodedPassword);

        UserDTO result = authController.register(newUserRegisterDto);
        assertNotNull(result);
        assertEquals(newUserRegisterDto.getDni(), result.getDni());
        assertEquals(newUserRegisterDto.getEmail(), result.getEmail());
        assertEquals(newUserRegisterDto.getName(), result.getName());
        assertEquals(newUserRegisterDto.getLastName(), result.getLastName());
    }

    @Test
    void register_whenPasswordsDoNotMatch_throwsConflictException() {
        UserRegisterDTO newUserRegisterDto = new UserRegisterDTO();
        newUserRegisterDto.setEmail("test@example.com");
        newUserRegisterDto.setDni("12345678A");
        newUserRegisterDto.setPassword("pass123");
        newUserRegisterDto.setRepeatPassword("pass");

        assertThrows(ConflictException.class, () -> {
            authController.register(newUserRegisterDto);
        });
    }

    @Test
    void register_whenExistsDni_throwsBadRequestException() {
        UserRegisterDTO newUserRegisterDto = new UserRegisterDTO();
        newUserRegisterDto.setEmail("test@example.com");
        newUserRegisterDto.setDni("12345678A");
        newUserRegisterDto.setPassword("pass123");
        newUserRegisterDto.setRepeatPassword("pass123");

        when(this.userRepository.existsByDni("12345678A")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> {
            authController.register(newUserRegisterDto);
        });
    }

    @Test
    void register_whenExistsEmail_throwsBadRequestException() {
        UserRegisterDTO newUserRegisterDto = new UserRegisterDTO();
        newUserRegisterDto.setEmail("test@example.com");
        newUserRegisterDto.setDni("12345678A");
        newUserRegisterDto.setPassword("pass123");
        newUserRegisterDto.setRepeatPassword("pass123");

        when(this.userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> {
            authController.register(newUserRegisterDto);
        });
    }

    @Test
    void register_whenExistsEmailAndDni_throwsBadRequestException() {
        UserRegisterDTO newUserRegisterDto = new UserRegisterDTO();
        newUserRegisterDto.setEmail("test@example.com");
        newUserRegisterDto.setDni("12345678A");
        newUserRegisterDto.setPassword("pass123");
        newUserRegisterDto.setRepeatPassword("pass123");

        when(this.userRepository.existsByDni("12345678A")).thenReturn(true);
        when(this.userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> {
            authController.register(newUserRegisterDto);
        });
    }



}
