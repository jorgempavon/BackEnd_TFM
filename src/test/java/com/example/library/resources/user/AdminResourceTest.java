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
    private static final Long EXAMPLE_ID = 2L;
    private static final String EXAMPLE_NAME = "example";
    private static final String EXAMPLE_EMAIL = "test@example.com";
    private static final String EXAMPLE_DNI = "12345678A";
    private static final String EXAMPLE_LAST_NAME = "last name example";
    private static final String ROL = "admin";
    private static final UserCreateDTO USER_CREATE_DTO = new UserCreateDTO(EXAMPLE_DNI, EXAMPLE_EMAIL, EXAMPLE_NAME, EXAMPLE_LAST_NAME);
    private static final UserDTO USER_DTO = new UserDTO(EXAMPLE_ID,EXAMPLE_NAME, EXAMPLE_EMAIL, EXAMPLE_DNI, EXAMPLE_LAST_NAME,ROL);

    @Test
    void createAdmin_successful(){
        when(this.adminService.create(USER_CREATE_DTO)).thenReturn(USER_DTO);
        ResponseEntity<?> result =adminResource.create(USER_CREATE_DTO);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertTrue(result.getBody() instanceof UserDTO);
    }

    @Test
    void  deleteAdmin_successful(){
        doNothing().when(adminService).delete(EXAMPLE_ID);
        ResponseEntity<?> result = adminResource.delete(EXAMPLE_ID);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}
