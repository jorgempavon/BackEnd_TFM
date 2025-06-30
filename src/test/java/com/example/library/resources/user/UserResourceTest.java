package com.example.library.resources.user;

import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.api.exceptions.models.UnauthorizedException;
import com.example.library.api.resources.user.UserResource;
import com.example.library.config.CustomUserDetails;
import com.example.library.entities.dto.user.*;
import com.example.library.services.user.UserService;
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
    private UserService userService;
    @Mock
    private CustomUserDetails mockUserDetails;
    @InjectMocks
    private UserResource userResource;

    private static final Long EXAMPLE_ID = 2L;
    private static final String EXAMPLE_NAME = "example";
    private static final String EXAMPLE_EMAIL = "test@example.com";
    private static final String EXAMPLE_DNI = "12345678A";
    private static final String EXAMPLE_PASS = "pass123";
    private static final String EXAMPLE_LAST_NAME = "last name example";
    private static final UserDTO USER_DTO = new UserDTO(EXAMPLE_ID,EXAMPLE_NAME, EXAMPLE_EMAIL, EXAMPLE_DNI, EXAMPLE_LAST_NAME,"client");
    private static final List<UserDTO> LIST_USERS_DTO = List.of(USER_DTO);
    private static final LoginDTO LOGIN_DTO = new LoginDTO(
            EXAMPLE_EMAIL,EXAMPLE_PASS
    );
    @Test
    void findByIdSuccessful(){
        when(this.userService.findById(EXAMPLE_ID)).thenReturn(USER_DTO);
        ResponseEntity<?> result = userResource.findById(EXAMPLE_ID);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody() instanceof UserDTO);
    }
    @Test
    void findByNameAndDniAndEmailSuccessful(){
        when(this.userService.findByNameAndDniAndEmail(EXAMPLE_NAME,EXAMPLE_DNI,EXAMPLE_EMAIL)).thenReturn(LIST_USERS_DTO);

        ResponseEntity<?> result = userResource.findByNameAndDniAndEmail(EXAMPLE_NAME,EXAMPLE_DNI,EXAMPLE_EMAIL);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(result.getBody(),LIST_USERS_DTO);
    }

    @Test
    void updateAdminDtoSuccessful(){
        UserAdminUpdateDTO userAdminUpdateDTO = new UserAdminUpdateDTO(EXAMPLE_DNI,EXAMPLE_EMAIL,true,
                EXAMPLE_NAME,EXAMPLE_LAST_NAME);
        when(this.userService.update(EXAMPLE_ID,userAdminUpdateDTO)).thenReturn(USER_DTO);

        ResponseEntity<?> result = userResource.update(EXAMPLE_ID,userAdminUpdateDTO);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(result.getBody(),USER_DTO);
    }

    @Test
    void updateSelfDtoSuccessful(){
        String pass = "pass4342";
        UserSelfUpdateDTO userSelfUpdateDTO = new UserSelfUpdateDTO(EXAMPLE_DNI,EXAMPLE_EMAIL,"otherPass",
                pass,pass, EXAMPLE_NAME,EXAMPLE_LAST_NAME);
        when(this.userService.update(EXAMPLE_ID,userSelfUpdateDTO)).thenReturn(USER_DTO);
        when(this.mockUserDetails.getId()).thenReturn(EXAMPLE_ID);

        ResponseEntity<?> result = userResource.update(mockUserDetails,userSelfUpdateDTO);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(result.getBody(),USER_DTO);
    }

    @Test
    void loginSuccessful() {
        String mockJwt = "mockedJwtToken";
        SessionDTO sessionDTO = new SessionDTO();
        sessionDTO.setEmail(EXAMPLE_EMAIL);
        sessionDTO.setJwt(mockJwt);
        when(this.userService.login(LOGIN_DTO)).thenReturn(sessionDTO);

        ResponseEntity<?> result = userResource.login(LOGIN_DTO);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void loginWhenEmptyLoginDtoThrowsException() {
        LoginDTO emptyloginDTO = new LoginDTO();

        assertThrows(Exception.class, () -> {
            userResource.login(emptyloginDTO);
        });
    }
    
    @Test
    void logOutSuccessful(){
        String exampleToken = "Bearer sdjinew0vw-rewrwegrgrge0cmtgtrgrtgtgnbynhyh09";
        doNothing().when(userService).logOut(anyString());

        ResponseEntity<?> result = userResource.logOut(exampleToken);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void logOutWhenTokenIsNullThrowsUnauthorizedException(){
        assertThrows(UnauthorizedException.class, () -> {
            userResource.logOut(null);
        });
    }
}
