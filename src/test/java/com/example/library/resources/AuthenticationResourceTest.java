package com.example.library.resources;

import com.example.library.api.exceptions.models.BadRequestException;
import com.example.library.api.exceptions.models.ConflictException;
import com.example.library.api.exceptions.models.UnauthorizedException;
import com.example.library.api.resources.AuthenticationResource;
import com.example.library.services.AuthenticationService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationResourceTest {
    @Mock
    private AuthenticationService authService;
    @InjectMocks
    private AuthenticationResource authResource;
    private final String EXAMPLE_NAME = "example";
    private final String EXAMPLE_EMAIL = "test@example.com";
    private final String EXAMPLE_PASS = "pass123";
    private final String EXAMPLE_DNI = "12345678A";
    private final String EXAMPLE_LAST_NAME = "last name example";
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

    @Test
    void register_successful() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName(EXAMPLE_NAME);
        userDTO.setEmail(EXAMPLE_EMAIL);
        userDTO.setDni(EXAMPLE_DNI);

        when(this.authService.register(userRegisterDTO)).thenReturn(userDTO);

        ResponseEntity<?> result = authResource.register(userRegisterDTO);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertTrue(result.getBody() instanceof UserDTO);

        UserDTO resultDto = (UserDTO) result.getBody();
        assertEquals(userDTO.getEmail(), resultDto.getEmail());
        assertEquals(userDTO.getDni(), resultDto.getDni());
    }

    @Test
    void register_whenPasswordsDoNotMatch_throwsConflictException() {
        when(this.authService.register(userRegisterDTO))
                .thenThrow(new ConflictException("Las contraseñas proporcionadas no coinciden"));

        assertThrows(ConflictException.class, () -> {
            authResource.register(userRegisterDTO);
        });
    }

    @Test
    void register_whenDniExists_throwsBadRequestException() {
        when(this.authService.register(userRegisterDTO))
                .thenThrow(new BadRequestException("El Dni proporcionado pertenece a otro usuario. Por favor, inténtelo de nuevo"));

        assertThrows(BadRequestException.class, () -> {
            authResource.register(userRegisterDTO);
        });
    }

    @Test
    void register_whenEmailExists_throwsBadRequestException() {
        when(this.authService.register(userRegisterDTO))
                .thenThrow(new BadRequestException("El Email proporcionado pertenece a otro usuario. Por favor, inténtelo de nuevo"));

        assertThrows(BadRequestException.class, () -> {
            authResource.register(userRegisterDTO);
        });
    }

    @Test
    void register_whenEmptyDto_throwsException() {
        UserRegisterDTO newEmptyUserRegisterDTO = new UserRegisterDTO();

        assertThrows(Exception.class, () -> {
            authResource.register(newEmptyUserRegisterDTO);
        });
    }

    @Test
    void login_successful() {
        String MOCKED_JWT = "mockedJwtToken";
        SessionDTO sessionDTO = new SessionDTO();
        sessionDTO.setEmail(EXAMPLE_EMAIL);
        sessionDTO.setJwt(MOCKED_JWT);
        when(this.authService.login(loginDTO)).thenReturn(sessionDTO);

        ResponseEntity<?> result = authResource.login(loginDTO);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void login_whenEmptyLoginDto_throwsException() {
        LoginDTO emptyloginDTO = new LoginDTO();

        assertThrows(Exception.class, () -> {
            authResource.login(emptyloginDTO);
        });
    }

    @Test
    void login_whenInvalidCredentials_throwsUnauthorizedException(){
        when(this.authService.login(loginDTO))
                .thenThrow(new UnauthorizedException("El email o contraseña proporcionados son incorrectos"));

        assertThrows(UnauthorizedException.class, () -> {
            authResource.login(loginDTO);
        });
    }
    @Test
    void logOut_Successful(){
        String EXAMPLE_TOKEN = "Bearer sdjinew0vw-rewrwegrgrge0cmtgtrgrtgtgnbynhyh09";
        doNothing().when(authService).logOut(anyString());

        ResponseEntity<?> result = authResource.logOut(EXAMPLE_TOKEN);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void logOut_whenTokenIsNull_throwsUnauthorizedException(){
        assertThrows(UnauthorizedException.class, () -> {
            authResource.logOut(null);
        });
    }
}
