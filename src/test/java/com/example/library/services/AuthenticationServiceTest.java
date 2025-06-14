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
    private final String exampleName = "example";
    private final String exampleLastName = "last name example";
    private final String exampleEmail = "test@example.com";
    private final String examplePass = "pass123";
    private final String exampleDni = "12345678A";
    private final String exampleTkn = "sdjinew0vw-rewrwegrgrge0cmtgtrgrtgtgnbynhyh09";
    private final UserRegisterDTO userRegisterDTO = new UserRegisterDTO(
            exampleDni,
            exampleEmail,
            examplePass,
            examplePass,
            exampleName,
            exampleLastName
    );
    private final LoginDTO loginDTO = new LoginDTO(
            exampleEmail,examplePass
    );
    private final String exampleEncodedPass = "encodedPassword";

    @Test
    void register_successful() {
        UserDTO userDTO  = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail(exampleEmail);
        userDTO.setName(exampleName);
        userDTO.setLastName(exampleLastName);
        userDTO.setDni(exampleDni);
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
        newUserRegisterDto.setName(exampleName);
        newUserRegisterDto.setName(exampleLastName);
        newUserRegisterDto.setEmail(exampleEmail);
        newUserRegisterDto.setDni(exampleDni);
        newUserRegisterDto.setPassword(examplePass);
        String exampleBadPass = "pass";
        newUserRegisterDto.setRepeatPassword(exampleBadPass);

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
        when(userDetailsController.loadUserByUsername(exampleEmail)).thenReturn(mockUserDetails);
        when(mockUserDetails.getPassword()).thenReturn(exampleEncodedPass);
        when(passwordEncoder.matches(examplePass, exampleEncodedPass)).thenReturn(true);
        String mockJwt = "mockedJwtToken";
        when(jwtService.generateToken(mockUserDetails)).thenReturn(mockJwt);

        SessionDTO sessionDTO = this.authService.login(loginDTO);

        assertEquals(mockJwt, sessionDTO.getJwt());
    }

    @Test
    void login_whenNotExistsEmail_throwsUnauthorizedException(){
        when(userDetailsController.loadUserByUsername(exampleEmail)).thenThrow(
                new UnauthorizedException("El email o contraseña proporcionados son incorrectos")
        );

        assertThrows(UnauthorizedException.class, () -> {
            authService.login(loginDTO);
        });
    }

    @Test
    void login_whenNotMatchPassword_throwsUnauthorizedException(){
        CustomUserDetails mockUserDetails = mock(CustomUserDetails.class);
        when(userDetailsController.loadUserByUsername(exampleEmail)).thenReturn(mockUserDetails);
        when(mockUserDetails.getPassword()).thenReturn(exampleEncodedPass);
        when(passwordEncoder.matches(examplePass, exampleEncodedPass)).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> {
            authService.login(loginDTO);
        });
    }
    @Test
    void logOut_Successful(){
        CustomUserDetails mockUserDetails = mock(CustomUserDetails.class);

        when(this.jwtService.extractUsername(exampleTkn)).thenReturn(exampleEmail);
        when(this.userDetailsController.loadUserByUsername(exampleEmail)).thenReturn(mockUserDetails);
        when(this.jwtService.isTokenValid(exampleTkn, mockUserDetails)).thenReturn(true);

        this.authService.logOut(exampleTkn);
    }
    @Test
    void logOut_whenUserNotExists_throwsUnathorizedException(){
        when(this.jwtService.extractUsername(exampleTkn)).thenThrow(UnauthorizedException.class);

        assertThrows(UnauthorizedException.class, () -> {
            authService.logOut(exampleTkn);
        });
    }
}
