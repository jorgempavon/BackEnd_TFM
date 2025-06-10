package com.example.library.controller;

import com.example.library.api.exceptions.models.ConflictException;
import com.example.library.api.exceptions.models.BadRequestException;
import com.example.library.api.exceptions.models.UnauthorizedException;
import com.example.library.config.CustomUserDetails;
import com.example.library.config.JwtController;
import com.example.library.entities.dto.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationControllerTest {
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtController jwtController;
    @Mock
    private UserDetailsService userDetailsController;
    @Mock
    private UserController userController;
    @InjectMocks
    private AuthenticationController authController;
    private final String EXAMPLE_NAME = "example";
    private final String EXAMPLE_LAST_NAME = "last name example";
    private final String EXAMPLE_EMAIL = "test@example.com";
    private final String EXAMPLE_PASSWORD = "pass123";
    private final String EXAMPLE_DNI = "12345678A";
    private final UserRegisterDTO userRegisterDTO = new UserRegisterDTO(
            EXAMPLE_DNI,
            EXAMPLE_EMAIL,
            EXAMPLE_PASSWORD,
            EXAMPLE_PASSWORD,
            EXAMPLE_NAME,
            EXAMPLE_LAST_NAME
    );
    private final LoginDTO loginDTO = new LoginDTO(
            EXAMPLE_EMAIL,EXAMPLE_PASSWORD
    );
    private final String EXAMPLE_ENCODED_PASSWORD = "encodedPassword";

    @Test
    void register_successful() {
        UserDTO userDTO  = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail(EXAMPLE_EMAIL);
        userDTO.setName(EXAMPLE_NAME);
        userDTO.setLastName(EXAMPLE_LAST_NAME);
        userDTO.setDni(EXAMPLE_DNI);
        userDTO.setIsAdmin(false);

        when(this.userController.create(any(UserRegisterDTO.class)))
                .thenReturn(userDTO);

        UserDTO result = authController.register(userRegisterDTO);
        assertNotNull(result);
        assertEquals(userRegisterDTO.getDni(), result.getDni());
        assertEquals(userRegisterDTO.getEmail(), result.getEmail());
        assertEquals(userRegisterDTO.getName(), result.getName());
        assertEquals(userRegisterDTO.getLastName(), result.getLastName());
    }

    @Test
    void register_whenPasswordsDoNotMatch_throwsConflictException() {
        UserRegisterDTO newUserRegisterDto = new UserRegisterDTO();
        newUserRegisterDto.setName(EXAMPLE_NAME);
        newUserRegisterDto.setName(EXAMPLE_LAST_NAME);
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
    void register_whenUserExists_throwsBadRequestException() {
        when(this.userController.create(userRegisterDTO))
                .thenThrow(BadRequestException.class);

        assertThrows(BadRequestException.class, () -> {
            authController.register(userRegisterDTO);
        });
    }

    @Test
    void login_successful(){
        CustomUserDetails mockUserDetails = mock(CustomUserDetails.class);
        when(userDetailsController.loadUserByUsername(EXAMPLE_EMAIL)).thenReturn(mockUserDetails);
        when(mockUserDetails.getPassword()).thenReturn(EXAMPLE_ENCODED_PASSWORD);
        when(passwordEncoder.matches(EXAMPLE_PASSWORD, EXAMPLE_ENCODED_PASSWORD)).thenReturn(true);
        String MOCKED_JWT = "mockedJwtToken";
        when(jwtController.generateToken(mockUserDetails)).thenReturn(MOCKED_JWT);

        SessionDTO sessionDTO = this.authController.login(loginDTO);

        assertEquals(MOCKED_JWT, sessionDTO.getJwt());
    }

    @Test
    void login_whenNotExistsEmail_throwsUnauthorizedException(){
        when(userDetailsController.loadUserByUsername(EXAMPLE_EMAIL)).thenThrow(
                new UnauthorizedException("El email o contraseña proporcionados son incorrectos")
        );

        assertThrows(UnauthorizedException.class, () -> {
            authController.login(loginDTO);
        });
    }

    @Test
    void login_whenNotMatchPassword_throwsUnauthorizedException(){
        CustomUserDetails mockUserDetails = mock(CustomUserDetails.class);
        when(userDetailsController.loadUserByUsername(EXAMPLE_EMAIL)).thenReturn(mockUserDetails);
        when(mockUserDetails.getPassword()).thenReturn(EXAMPLE_ENCODED_PASSWORD);
        when(passwordEncoder.matches(EXAMPLE_PASSWORD, EXAMPLE_ENCODED_PASSWORD)).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> {
            authController.login(loginDTO);
        });
    }

}
