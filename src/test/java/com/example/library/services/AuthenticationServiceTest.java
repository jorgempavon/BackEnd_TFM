package com.example.library.services;

import com.example.library.api.exceptions.models.ConflictException;
import com.example.library.api.exceptions.models.BadRequestException;
import com.example.library.api.exceptions.models.UnauthorizedException;
import com.example.library.config.CustomUserDetails;
import com.example.library.config.JwtService;
import com.example.library.entities.dto.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserDetailsService userDetailsController;
    @Mock
    private UserService userService;
    @InjectMocks
    private AuthenticationService authService;
    private final String EXAMPLE_NAME = "example";
    private final String EXAMPLE_LAST_NAME = "last name example";
    private final String EXAMPLE_EMAIL = "test@example.com";
    private final String EXAMPLE_PASS = "pass123";
    private final String EXAMPLE_DNI = "12345678A";
    private final String EXAMPLE_TOKEN = "sdjinew0vw-rewrwegrgrge0cmtgtrgrtgtgnbynhyh09";
    private final UserRegisterDTO userRegisterDTO = new UserRegisterDTO(
            EXAMPLE_DNI,
            EXAMPLE_EMAIL,
            EXAMPLE_PASS,
            EXAMPLE_PASS,
            EXAMPLE_NAME,
            EXAMPLE_LAST_NAME
    );
    private final LoginDTO loginDTO = new LoginDTO(
            EXAMPLE_EMAIL,EXAMPLE_PASS
    );
    private final String EXAMPLE_ENCODED_PASS = "encodedPassword";

    @Test
    void register_successful() {
        UserDTO userDTO  = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail(EXAMPLE_EMAIL);
        userDTO.setName(EXAMPLE_NAME);
        userDTO.setLastName(EXAMPLE_LAST_NAME);
        userDTO.setDni(EXAMPLE_DNI);
        userDTO.setIsAdmin(false);

        when(this.userService.create(any(UserRegisterDTO.class)))
                .thenReturn(userDTO);

        UserDTO result = authService.register(userRegisterDTO);
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
        newUserRegisterDto.setPassword(EXAMPLE_PASS);
        String EXAMPLE_BAD_PASS = "pass";
        newUserRegisterDto.setRepeatPassword(EXAMPLE_BAD_PASS);

        assertThrows(ConflictException.class, () -> {
            authService.register(newUserRegisterDto);
        });
    }

    @Test
    void register_whenUserExists_throwsBadRequestException() {
        when(this.userService.create(userRegisterDTO))
                .thenThrow(BadRequestException.class);

        assertThrows(BadRequestException.class, () -> {
            authService.register(userRegisterDTO);
        });
    }

    @Test
    void login_successful(){
        CustomUserDetails mockUserDetails = mock(CustomUserDetails.class);
        when(userDetailsController.loadUserByUsername(EXAMPLE_EMAIL)).thenReturn(mockUserDetails);
        when(mockUserDetails.getPassword()).thenReturn(EXAMPLE_ENCODED_PASS);
        when(passwordEncoder.matches(EXAMPLE_PASS, EXAMPLE_ENCODED_PASS)).thenReturn(true);
        String MOCKED_JWT = "mockedJwtToken";
        when(jwtService.generateToken(mockUserDetails)).thenReturn(MOCKED_JWT);

        SessionDTO sessionDTO = this.authService.login(loginDTO);

        assertEquals(MOCKED_JWT, sessionDTO.getJwt());
    }

    @Test
    void login_whenNotExistsEmail_throwsUnauthorizedException(){
        when(userDetailsController.loadUserByUsername(EXAMPLE_EMAIL)).thenThrow(
                new UnauthorizedException("El email o contraseña proporcionados son incorrectos")
        );

        assertThrows(UnauthorizedException.class, () -> {
            authService.login(loginDTO);
        });
    }

    @Test
    void login_whenNotMatchPassword_throwsUnauthorizedException(){
        CustomUserDetails mockUserDetails = mock(CustomUserDetails.class);
        when(userDetailsController.loadUserByUsername(EXAMPLE_EMAIL)).thenReturn(mockUserDetails);
        when(mockUserDetails.getPassword()).thenReturn(EXAMPLE_ENCODED_PASS);
        when(passwordEncoder.matches(EXAMPLE_PASS, EXAMPLE_ENCODED_PASS)).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> {
            authService.login(loginDTO);
        });
    }
    @Test
    void logOut_Successful(){
        CustomUserDetails mockUserDetails = mock(CustomUserDetails.class);

        when(this.jwtService.extractUsername(EXAMPLE_TOKEN)).thenReturn(EXAMPLE_EMAIL);
        when(this.userDetailsController.loadUserByUsername(EXAMPLE_EMAIL)).thenReturn(mockUserDetails);
        when(this.jwtService.isTokenValid(EXAMPLE_TOKEN, mockUserDetails)).thenReturn(true);

        this.authService.logOut(EXAMPLE_TOKEN);
    }
    @Test
    void logOut_whenUserNotExists_throwsUnathorizedException(){
        when(this.jwtService.extractUsername(EXAMPLE_TOKEN)).thenThrow(UnauthorizedException.class);

        assertThrows(UnauthorizedException.class, () -> {
            authService.logOut(EXAMPLE_TOKEN);
        });
    }
}
