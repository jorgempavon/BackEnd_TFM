package com.example.library.view;

import com.example.library.api.exceptions.models.BadRequestException;
import com.example.library.api.exceptions.models.ConflictException;
import com.example.library.api.exceptions.models.UnauthorizedException;
import com.example.library.api.view.AuthenticationView;
import com.example.library.controller.AuthenticationController;
import com.example.library.entities.dto.LoginDTO;
import com.example.library.entities.dto.SessionDTO;
import com.example.library.entities.dto.UserDTO;
import com.example.library.entities.dto.UserRegisterDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationViewTest {
    @Mock
    private AuthenticationController authController;
    @InjectMocks
    private AuthenticationView authView;
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

        UserDTO userDTO = new UserDTO();
        userDTO.setName(EXAMPLE_NAME);
        userDTO.setEmail(EXAMPLE_EMAIL);
        userDTO.setDni(EXAMPLE_DNI);

        when(this.authController.register(newUserRegisterDto)).thenReturn(userDTO);

        ResponseEntity<?> result = authView.register(newUserRegisterDto);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertTrue(result.getBody() instanceof UserDTO);

        UserDTO resultDto = (UserDTO) result.getBody();
        assertEquals(userDTO.getEmail(), resultDto.getEmail());
        assertEquals(userDTO.getDni(), resultDto.getDni());
    }

    @Test
    void register_whenPasswordsDoNotMatch_throwsConflictException() {
        UserRegisterDTO newUserRegisterDto = new UserRegisterDTO();
        newUserRegisterDto.setName(EXAMPLE_NAME);
        newUserRegisterDto.setEmail(EXAMPLE_EMAIL);
        newUserRegisterDto.setDni(EXAMPLE_DNI);
        newUserRegisterDto.setPassword(EXAMPLE_PASSWORD);
        newUserRegisterDto.setRepeatPassword(EXAMPLE_PASSWORD);

        when(this.authController.register(newUserRegisterDto))
                .thenThrow(new ConflictException("Las contraseñas proporcionadas no coinciden"));

        assertThrows(ConflictException.class, () -> {
            authView.register(newUserRegisterDto);
        });
    }

    @Test
    void register_whenDniExists_throwsBadRequestException() {
        UserRegisterDTO newUserRegisterDto = new UserRegisterDTO();
        newUserRegisterDto.setName(EXAMPLE_NAME);
        newUserRegisterDto.setEmail(EXAMPLE_EMAIL);
        newUserRegisterDto.setDni(EXAMPLE_DNI);
        newUserRegisterDto.setPassword(EXAMPLE_PASSWORD);
        newUserRegisterDto.setRepeatPassword(EXAMPLE_PASSWORD);

        when(this.authController.register(newUserRegisterDto))
                .thenThrow(new BadRequestException("El Dni proporcionado pertenece a otro usuario. Por favor, inténtelo de nuevo"));

        assertThrows(BadRequestException.class, () -> {
            authView.register(newUserRegisterDto);
        });
    }

    @Test
    void register_whenEmailExists_throwsBadRequestException() {
        UserRegisterDTO newUserRegisterDto = new UserRegisterDTO();
        newUserRegisterDto.setName(EXAMPLE_NAME);
        newUserRegisterDto.setEmail(EXAMPLE_EMAIL);
        newUserRegisterDto.setDni(EXAMPLE_DNI);
        newUserRegisterDto.setPassword(EXAMPLE_PASSWORD);
        newUserRegisterDto.setRepeatPassword(EXAMPLE_PASSWORD);

        when(this.authController.register(newUserRegisterDto))
                .thenThrow(new BadRequestException("El Email proporcionado pertenece a otro usuario. Por favor, inténtelo de nuevo"));

        assertThrows(BadRequestException.class, () -> {
            authView.register(newUserRegisterDto);
        });
    }

    @Test
    void register_whenEmptyDto_throwsException() {
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO();

        assertThrows(Exception.class, () -> {
            authView.register(userRegisterDTO);
        });
    }

    @Test
    void login_successful() {
        String MOCKED_JWT = "mockedJwtToken";
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail(EXAMPLE_EMAIL);
        loginDTO.setPassword(EXAMPLE_PASSWORD);

        SessionDTO sessionDTO = new SessionDTO();
        sessionDTO.setEmail(EXAMPLE_EMAIL);
        sessionDTO.setJwt(MOCKED_JWT);
        when(this.authController.login(loginDTO)).thenReturn(sessionDTO);

        ResponseEntity<?> result = authView.login(loginDTO);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void login_whenEmptyLoginDto_throwsException() {
        LoginDTO loginDTO = new LoginDTO();

        assertThrows(Exception.class, () -> {
            authView.login(loginDTO);
        });
    }

    @Test
    void login_whenInvalidCredentials_throwsUnauthorizedException(){
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail(EXAMPLE_EMAIL);
        loginDTO.setPassword(EXAMPLE_PASSWORD);

        when(this.authController.login(loginDTO))
                .thenThrow(new UnauthorizedException("El email o contraseña proporcionados son incorrectos"));

        assertThrows(UnauthorizedException.class, () -> {
            authView.login(loginDTO);
        });
    }
}
