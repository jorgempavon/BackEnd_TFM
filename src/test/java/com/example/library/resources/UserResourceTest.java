package com.example.library.resources;

import com.example.library.api.exceptions.models.BadRequestException;
import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.api.resources.UserResource;
import com.example.library.config.CustomUserDetails;
import com.example.library.entities.dto.UserAdminUpdateDTO;
import com.example.library.entities.dto.UserSelfUpdateDTO;
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
    @Mock
    private CustomUserDetails mockUserDetails;
    private static final Long exampleId = 2L;
    private static final String exampleName = "example";
    private static final String exampleEmail = "test@example.com";
    private static final String exampleDni = "12345678A";
    private static final String exampleLastName = "last name example";
    private static final Boolean exampleIsAdmin = true;
    private static final UserCreateDTO userCreateDto = new UserCreateDTO(exampleDni, exampleEmail, exampleName, exampleLastName,exampleIsAdmin);
    private static final UserDTO userDTO = new UserDTO(exampleId,exampleName, exampleEmail, exampleDni, exampleLastName,exampleIsAdmin);
    private static final List<UserDTO> listUsersDto = List.of(userDTO);

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
        doNothing().when(userService).delete(exampleId);
        ResponseEntity<?> result = userResource.delete(exampleId);
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
        when(this.userService.findByNameAndDniAndEmail(exampleName,exampleDni,exampleEmail)).thenReturn(listUsersDto);

        ResponseEntity<?> result = userResource.findByNameAndDniAndEmail(exampleName,exampleDni,exampleEmail);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(result.getBody(),listUsersDto);
    }

    @Test
    void updateAdminDto_successful(){
        UserAdminUpdateDTO userAdminUpdateDTO = new UserAdminUpdateDTO(exampleDni,exampleEmail,true,
                exampleName,exampleLastName,true);
        when(this.userService.update(exampleId,userAdminUpdateDTO)).thenReturn(userDTO);

        ResponseEntity<?> result = userResource.update(exampleId,userAdminUpdateDTO);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(result.getBody(),userDTO);
    }

    @Test
    void updateSelfDto_successful(){
        String pass = "pass4342";
        UserSelfUpdateDTO userSelfUpdateDTO = new UserSelfUpdateDTO(exampleDni,exampleEmail,"otherPass",
                pass,pass, exampleName,exampleLastName);
        when(this.userService.update(exampleId,userSelfUpdateDTO)).thenReturn(userDTO);
        when(this.mockUserDetails.getId()).thenReturn(exampleId);

        ResponseEntity<?> result = userResource.update(mockUserDetails,userSelfUpdateDTO);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(result.getBody(),userDTO);
    }
}
