package com.example.library.resources;

import com.example.library.api.exceptions.models.BadRequestException;
import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.api.resources.UserResource;
import com.example.library.services.UserService;
import com.example.library.entities.dto.UserCreateDTO;
import com.example.library.entities.dto.UserDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserResourceTest {
    @Mock
    private UserService userService;
    @InjectMocks
    private UserResource userResource;
    private final Long EXAMPLE_ID = 2L;
    private final String EXAMPLE_NAME = "example";
    private final String EXAMPLE_EMAIL = "test@example.com";
    private final String EXAMPLE_DNI = "12345678A";
    private final String EXAMPLE_LAST_NAME = "last name example";
    private final Boolean EXAMPLE_IS_ADMIN = true;
    private final String EXAMPLE_PASSWORD = "pass123";
    private final UserCreateDTO userCreateDto = new UserCreateDTO(EXAMPLE_DNI, EXAMPLE_EMAIL, EXAMPLE_NAME, EXAMPLE_LAST_NAME,EXAMPLE_IS_ADMIN);
    private final UserDTO userDTO = new UserDTO(EXAMPLE_ID,EXAMPLE_NAME, EXAMPLE_EMAIL, EXAMPLE_DNI, EXAMPLE_LAST_NAME,EXAMPLE_IS_ADMIN);
    //private final UserUpdateDTO userClientUpdateDTO = new UserUpdateDTO(EXAMPLE_DNI,EXAMPLE_EMAIL,EXAMPLE_PASSWORD
    //        ,EXAMPLE_PASSWORD,EXAMPLE_NAME,EXAMPLE_LAST_NAME,false);
    private final List<UserDTO> listUsersDto = List.of(userDTO);

    @Test
    void findById_successful(){
        when(this.userService.findById(EXAMPLE_ID)).thenReturn(userDTO);
        ResponseEntity<?> result = userResource.findById(EXAMPLE_ID);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody() instanceof UserDTO);
    }

    @Test
    void findById_whenIdNotExists_throwsNotFoundException(){
        when(this.userService.findById(EXAMPLE_ID))
                .thenThrow(new NotFoundException("No existe el usuario con el id: "+EXAMPLE_ID.toString()));
        assertThrows(NotFoundException.class, () -> {
            userResource.findById(EXAMPLE_ID);
        });
    }

    @Test
    void createUser_successful(){
        when(this.userService.create(userCreateDto)).thenReturn(userDTO);
        ResponseEntity<?> result = userResource.create(userCreateDto);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertTrue(result.getBody() instanceof UserDTO);
    }

    @Test
    void createUser_whenUserExists_throwsBadRequestException(){
        when(this.userService.create(userCreateDto))
                .thenThrow(new BadRequestException("El dni o email proporcionados pertenecen a otro usuario"));
        assertThrows(BadRequestException.class, () -> {
            userResource.create(userCreateDto);
        });
    }

    @Test
    void createUser_whenEmailNotExists_throwsBadRequestException(){
        when(this.userService.create(userCreateDto))
                .thenThrow(new BadRequestException("El email proporcionado no existe"));
        assertThrows(BadRequestException.class, () -> {
            userResource.create(userCreateDto);
        });
    }

    @Test
    void  deleteUser_successful(){
        doNothing().when(userService).delete(EXAMPLE_ID);
        ResponseEntity<?> result = userResource.delete(EXAMPLE_ID);
        assertEquals(HttpStatus.OK, result.getStatusCode());
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
        when(this.userService.findByNameAndDniAndEmail(EXAMPLE_NAME,EXAMPLE_DNI,EXAMPLE_EMAIL)).thenReturn(listUsersDto);

        ResponseEntity<?> result = userResource.findByNameAndDniAndEmail(EXAMPLE_NAME,EXAMPLE_DNI,EXAMPLE_EMAIL);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(result.getBody(),listUsersDto);
    }
}
