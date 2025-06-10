package com.example.library.view;

import com.example.library.api.exceptions.models.ConflictException;
import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.api.view.UserView;
import com.example.library.controller.UserController;
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
    @Test
    void findById_successful(){
        User user = new User("example", "test@example.com", "12345678A", "Last example");
        UserDTO userDTO = user.getUserDTO(false);

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
}
