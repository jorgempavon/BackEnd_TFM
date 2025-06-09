package com.example.library.controller;

import com.example.library.api.exceptions.models.ConflictException;
import com.example.library.api.exceptions.models.BadRequestException;
import com.example.library.api.exceptions.models.UnauthorizedException;
import com.example.library.config.CustomUserDetails;
import com.example.library.config.JwtController;
import com.example.library.entities.dto.*;
import com.example.library.entities.repository.ClientRepository;
import com.example.library.entities.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;


import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    private UserRepository userRepository;
    @Mock
    private ClientRepository clientRepository;
    @InjectMocks
    private AuthenticationController authController;
    private final String EXAMPLE_NAME = "example";
    private final String EXAMPLE_EMAIL = "test@example.com";
    private final String EXAMPLE_PASSWORD = "pass123";
    private final String EXAMPLE_DNI = "12345678A";

    private final String EXAMPLE_ENCODED_PASSWORD = "encodedPassword";

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

    @Test
    void login_successful(){
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail(EXAMPLE_EMAIL);
        loginDTO.setPassword(EXAMPLE_PASSWORD);

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
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail(EXAMPLE_EMAIL);
        loginDTO.setPassword(EXAMPLE_PASSWORD);

        when(userDetailsController.loadUserByUsername(EXAMPLE_EMAIL)).thenThrow(
                new UnauthorizedException("El email o contraseña proporcionados son incorrectos")
        );

        assertThrows(UnauthorizedException.class, () -> {
            authController.login(loginDTO);
        });
    }

    @Test
    void login_whenNotMatchPassword_throwsUnauthorizedException(){
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail(EXAMPLE_EMAIL);
        loginDTO.setPassword(EXAMPLE_PASSWORD);

        CustomUserDetails mockUserDetails = mock(CustomUserDetails.class);
        when(userDetailsController.loadUserByUsername(EXAMPLE_EMAIL)).thenReturn(mockUserDetails);
        when(mockUserDetails.getPassword()).thenReturn(EXAMPLE_ENCODED_PASSWORD);
        when(passwordEncoder.matches(EXAMPLE_PASSWORD, EXAMPLE_ENCODED_PASSWORD)).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> {
            authController.login(loginDTO);
        });
    }

}
