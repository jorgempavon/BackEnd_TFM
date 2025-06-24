package com.example.library.resources;

import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.api.exceptions.models.UnauthorizedException;
import com.example.library.api.resources.user.UserResource;
import com.example.library.config.CustomUserDetails;
import com.example.library.entities.dto.user.*;
import com.example.library.services.user.UserSelfUpdateService;
import com.example.library.services.user.UserService;
import com.example.library.services.user.UserUpdateByAdminService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserResourceTest {
    @Mock
    private UserUpdateByAdminService userUpdateByAdminService;
    @Mock
    private UserSelfUpdateService userSelfUpdateService;
    @Mock
    private UserService userService;
    @Mock
    private CustomUserDetails mockUserDetails;
    @InjectMocks
    private UserResource userResource;

    private static final Long exampleId = 2L;
    private static final String exampleName = "example";
    private static final String exampleEmail = "test@example.com";
    private static final String exampleDni = "12345678A";
    private static final String examplePass = "pass123";
    private static final String exampleLastName = "last name example";
    private static final UserDTO userDTO = new UserDTO(exampleId,exampleName, exampleEmail, exampleDni, exampleLastName,"client");
    private static final List<UserDTO> listUsersDto = List.of(userDTO);
    private static final LoginDTO loginDTO = new LoginDTO(
            exampleEmail,examplePass
    );
    @Test
    void findById_successful(){
        when(this.userService.findById(exampleId)).thenReturn(userDTO);
        ResponseEntity<?> result = userResource.findById(exampleId);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody() instanceof UserDTO);
    }

    @Test
    void findById_whenIdNotExists_throwsNotFoundException(){
        when(this.userService.findById(exampleId))
                .thenThrow(new NotFoundException("No existe el usuario con el id: "+exampleId.toString()));
        assertThrows(NotFoundException.class, () -> {
            userResource.findById(exampleId);
        });
    }

    @Test
    void findByNameAndDniAndEmail_successful_parametersAreEmpty(){
        when(this.userService.findByNameAndDniAndEmail("","","")).thenReturn(listUsersDto);

        ResponseEntity<?> result = userResource.findByNameAndDniAndEmail("","","");
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(result.getBody(),listUsersDto);
    }

    @Test
    void findByNameAndDniAndEmail_successful_parametersAreNull(){
        when(this.userService.findByNameAndDniAndEmail(null,null,null)).thenReturn(listUsersDto);

        ResponseEntity<?> result = userResource.findByNameAndDniAndEmail(null,null,null);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(result.getBody(),listUsersDto);
    }

    @Test
    void findByNameAndDniAndEmail_successful(){
        when(this.userService.findByNameAndDniAndEmail(exampleName,exampleDni,exampleEmail)).thenReturn(listUsersDto);

        ResponseEntity<?> result = userResource.findByNameAndDniAndEmail(exampleName,exampleDni,exampleEmail);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(result.getBody(),listUsersDto);
    }

    @Test
    void updateAdminDto_successful(){
        UserAdminUpdateDTO userAdminUpdateDTO = new UserAdminUpdateDTO(exampleDni,exampleEmail,true,
                exampleName,exampleLastName);
        when(this.userUpdateByAdminService.update(exampleId,userAdminUpdateDTO)).thenReturn(userDTO);

        ResponseEntity<?> result = userResource.update(exampleId,userAdminUpdateDTO);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(result.getBody(),userDTO);
    }

    @Test
    void updateSelfDto_successful(){
        String pass = "pass4342";
        UserSelfUpdateDTO userSelfUpdateDTO = new UserSelfUpdateDTO(exampleDni,exampleEmail,"otherPass",
                pass,pass, exampleName,exampleLastName);
        when(this.userSelfUpdateService.update(exampleId,userSelfUpdateDTO)).thenReturn(userDTO);
        when(this.mockUserDetails.getId()).thenReturn(exampleId);

        ResponseEntity<?> result = userResource.update(mockUserDetails,userSelfUpdateDTO);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(result.getBody(),userDTO);
    }

    @Test
    void login_successful() {
        String mockJwt = "mockedJwtToken";
        SessionDTO sessionDTO = new SessionDTO();
        sessionDTO.setEmail(exampleEmail);
        sessionDTO.setJwt(mockJwt);
        when(this.userService.login(loginDTO)).thenReturn(sessionDTO);

        ResponseEntity<?> result = userResource.login(loginDTO);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void login_whenEmptyLoginDto_throwsException() {
        LoginDTO emptyloginDTO = new LoginDTO();

        assertThrows(Exception.class, () -> {
            userResource.login(emptyloginDTO);
        });
    }

    @Test
    void login_whenInvalidCredentials_throwsUnauthorizedException(){
        when(this.userService.login(loginDTO))
                .thenThrow(new UnauthorizedException("El email o contraseña proporcionados son incorrectos"));

        assertThrows(UnauthorizedException.class, () -> {
            userResource.login(loginDTO);
        });
    }
    @Test
    void logOut_Successful(){
        String exampleToken = "Bearer sdjinew0vw-rewrwegrgrge0cmtgtrgrtgtgnbynhyh09";
        doNothing().when(userService).logOut(anyString());

        ResponseEntity<?> result = userResource.logOut(exampleToken);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void logOut_whenTokenIsNull_throwsUnauthorizedException(){
        assertThrows(UnauthorizedException.class, () -> {
            userResource.logOut(null);
        });
    }
}
