package com.example.library.view;

import com.example.library.api.exceptions.models.BadRequestException;
import com.example.library.api.exceptions.models.ConflictException;
import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.api.view.UserView;
import com.example.library.controller.UserController;
import com.example.library.entities.dto.UserCreateDTO;
import com.example.library.entities.dto.UserDTO;
import com.example.library.entities.model.User;
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
public class UserViewTest {
    @Mock
    private UserController userController;
    @InjectMocks
    private UserView userView;
    private final Long EXAMPLE_ID = 2L;
    private final String EXAMPLE_NAME = "example";
    private final String EXAMPLE_EMAIL = "test@example.com";
    private final String EXAMPLE_DNI = "12345678A";
    private final String EXAMPLE_LAST_NAME = "last name example";
    private final Boolean EXAMPLE_IS_ADMIN = true;
    private final UserCreateDTO userCreateDto = new UserCreateDTO(EXAMPLE_DNI, EXAMPLE_EMAIL, EXAMPLE_NAME, EXAMPLE_LAST_NAME,EXAMPLE_IS_ADMIN);
    private final UserDTO userDTO = new UserDTO(EXAMPLE_NAME, EXAMPLE_EMAIL, EXAMPLE_DNI, EXAMPLE_LAST_NAME,EXAMPLE_IS_ADMIN);


    @Test
    void findById_successful(){
        when(this.userController.findById(EXAMPLE_ID)).thenReturn(userDTO);
        ResponseEntity<?> result = userView.findById(EXAMPLE_ID);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody() instanceof UserDTO);
    }

    @Test
    void findById_whenIdNotExists_throwsNotFoundException(){
        when(this.userController.findById(EXAMPLE_ID))
                .thenThrow(new NotFoundException("No existe el usuario con el id: "+EXAMPLE_ID.toString()));
        assertThrows(NotFoundException.class, () -> {
            userView.findById(EXAMPLE_ID);
        });
    }

    @Test
    void createUser_successful(){
        when(this.userController.create(userCreateDto)).thenReturn(userDTO);
        ResponseEntity<?> result = userView.create(userCreateDto);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertTrue(result.getBody() instanceof UserDTO);
    }

    @Test
    void createUser_whenUserExists_throwsBadRequestException(){
        when(this.userController.create(userCreateDto))
                .thenThrow(new BadRequestException("El dni o email proporcionados pertenecen a otro usuario"));
        assertThrows(BadRequestException.class, () -> {
            userView.create(userCreateDto);
        });
    }

    @Test
    void createUser_whenEmailNotExists_throwsBadRequestException(){
        when(this.userController.create(userCreateDto))
                .thenThrow(new BadRequestException("El email proporcionado no existe"));
        assertThrows(BadRequestException.class, () -> {
            userView.create(userCreateDto);
        });
    }
}
