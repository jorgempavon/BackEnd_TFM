package com.example.library.controller;

import com.example.library.api.exceptions.models.ConflictException;
import com.example.library.api.exceptions.models.BadRequestException;
import com.example.library.entities.dto.*;
import com.example.library.entities.repository.ClientRepository;
import com.example.library.entities.repository.UserRepository;
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
    private PasswordEncoder passwordEncoder;
    @Mock
    private ClientRepository clientRepository;
    @InjectMocks
    private AuthenticationController authController;
    private final String EXAMPLE_NAME = "example";
    private final String EXAMPLE_EMAIL = "test@example.com";
    private final String EXAMPLE_PASSWORD = "pass123";
    private final String EXAMPLE_DNI = "12345678A";

    @Test
    void register_successful() {
        UserRegisterDTO newUserRegisterDto = new UserRegisterDTO();
        newUserRegisterDto.setName(EXAMPLE_NAME);
        newUserRegisterDto.setEmail(EXAMPLE_EMAIL);
        newUserRegisterDto.setDni(EXAMPLE_DNI);
        newUserRegisterDto.setPassword(EXAMPLE_PASSWORD);
        newUserRegisterDto.setRepeatPassword(EXAMPLE_PASSWORD);

        when(this.userRepository.existsByDni(EXAMPLE_DNI)).thenReturn(false);
        when(this.userRepository.existsByEmail(EXAMPLE_EMAIL)).thenReturn(false);
        when(this.passwordEncoder.encode(EXAMPLE_PASSWORD)).thenReturn("hashedPass");
        System.out.println(clientRepository);

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
        newUserRegisterDto.setName(EXAMPLE_NAME);
        newUserRegisterDto.setEmail(EXAMPLE_EMAIL);
        newUserRegisterDto.setDni(EXAMPLE_DNI);
        newUserRegisterDto.setPassword(EXAMPLE_PASSWORD);
        String EXAMPLE_BAD_PASSWORD = "pass";
        newUserRegisterDto.setRepeatPassword(EXAMPLE_BAD_PASSWORD);

        assertThrows(ConflictException.class, () -> {
            authController.register(newUserRegisterDto);
        });
    }

    @Test
    void register_whenExistsDni_throwsBadRequestException() {
        UserRegisterDTO newUserRegisterDto = new UserRegisterDTO();
        newUserRegisterDto.setName(EXAMPLE_NAME);
        newUserRegisterDto.setEmail(EXAMPLE_EMAIL);
        newUserRegisterDto.setDni(EXAMPLE_DNI);
        newUserRegisterDto.setPassword(EXAMPLE_PASSWORD);
        newUserRegisterDto.setRepeatPassword(EXAMPLE_PASSWORD);

        when(this.userRepository.existsByDni(EXAMPLE_DNI)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> {
            authController.register(newUserRegisterDto);
        });
    }

    @Test
    void register_whenExistsEmail_throwsBadRequestException() {
        UserRegisterDTO newUserRegisterDto = new UserRegisterDTO();
        newUserRegisterDto.setName(EXAMPLE_NAME);
        newUserRegisterDto.setEmail(EXAMPLE_EMAIL);
        newUserRegisterDto.setDni(EXAMPLE_DNI);
        newUserRegisterDto.setPassword(EXAMPLE_PASSWORD);
        newUserRegisterDto.setRepeatPassword(EXAMPLE_PASSWORD);

        when(this.userRepository.existsByEmail(EXAMPLE_EMAIL)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> {
            authController.register(newUserRegisterDto);
        });
    }

    @Test
    void register_whenExistsEmailAndDni_throwsBadRequestException() {
        UserRegisterDTO newUserRegisterDto = new UserRegisterDTO();
        newUserRegisterDto.setName(EXAMPLE_NAME);
        newUserRegisterDto.setEmail(EXAMPLE_EMAIL);
        newUserRegisterDto.setDni(EXAMPLE_DNI);
        newUserRegisterDto.setPassword(EXAMPLE_PASSWORD);
        newUserRegisterDto.setRepeatPassword(EXAMPLE_PASSWORD);

        when(this.userRepository.existsByDni(EXAMPLE_DNI)).thenReturn(true);
        when(this.userRepository.existsByEmail(EXAMPLE_EMAIL)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> {
            authController.register(newUserRegisterDto);
        });
    }



}
