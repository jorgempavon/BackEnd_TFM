package com.example.library.resources.user;

import com.example.library.api.exceptions.models.BadRequestException;
import com.example.library.api.resources.user.AdminResource;
import com.example.library.entities.dto.user.UserCreateDTO;
import com.example.library.entities.dto.user.UserDTO;
import com.example.library.services.user.AdminService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
public class AdminResourceTest {
    @Mock
    private AdminService adminService;
    @InjectMocks
    private AdminResource adminResource;
    private static final Long exampleId = 2L;
    private static final String exampleName = "example";
    private static final String exampleEmail = "test@example.com";
    private static final String examplePass = "pass123";
    private static final String exampleDni = "12345678A";
    private static final String exampleLastName = "last name example";
    private static final String rol = "admin";
    private static final UserCreateDTO userCreateDto = new UserCreateDTO(exampleDni, exampleEmail, exampleName, exampleLastName);
    private static final UserDTO userDTO = new UserDTO(exampleId,exampleName, exampleEmail, exampleDni, exampleLastName,rol);

    @Test
    void createAdmin_successful(){
        when(this.adminService.create(userCreateDto)).thenReturn(userDTO);
        ResponseEntity<?> result =adminResource.create(userCreateDto);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertTrue(result.getBody() instanceof UserDTO);
    }

    @Test
    void  deleteAdmin_successful(){
        doNothing().when(adminService).delete(exampleId);
        ResponseEntity<?> result = adminResource.delete(exampleId);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}
