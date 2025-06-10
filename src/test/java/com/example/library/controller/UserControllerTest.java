package com.example.library.controller;

import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.entities.dto.UserDTO;
import com.example.library.entities.model.User;
import com.example.library.entities.repository.AdminRepository;
import com.example.library.entities.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private AdminRepository adminRepository;
    @InjectMocks
    private UserController userController;

    private final Long EXAMPLE_ID = 2L;


    @Test
    void findById_successful(){
        User user = new User("example", "test@example.com", "12345678A", "Last example");
        UserDTO userDTO = user.getUserDTO(false);
        when(this.userRepository.existsById(EXAMPLE_ID)).thenReturn(true);
        when(this.adminRepository.existsByUserId(EXAMPLE_ID)).thenReturn(false);
        when(userRepository.findById(EXAMPLE_ID)).thenReturn(Optional.of(user));
        UserDTO result = this.userController.findById(EXAMPLE_ID);

        assertEquals(userDTO.getDni(), result.getDni());
        assertEquals(userDTO.getEmail(), result.getEmail());
        assertEquals(userDTO.getName(), result.getName());
        assertEquals(userDTO.getLastName(), result.getLastName());
    }

    @Test
    void findById_whenNotExistsId_throwsNotFoundException(){
        when(this.userRepository.existsById(EXAMPLE_ID)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> {
            userController.findById(EXAMPLE_ID);
        });
    }

}
