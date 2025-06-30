package com.example.library.resources.user;

import com.example.library.api.exceptions.models.BadRequestException;
import com.example.library.api.exceptions.models.ConflictException;
import com.example.library.api.resources.user.ClientResource;
import com.example.library.entities.dto.user.UserCreateDTO;
import com.example.library.entities.dto.user.UserDTO;
import com.example.library.entities.dto.user.UserRegisterDTO;
import com.example.library.services.user.ClientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClientResourceTest {
    @Mock
    private ClientService clientService;
    @InjectMocks
    private ClientResource clientResource;
    private static final Long EXAMPLE_ID = 2L;
    private static final String EXAMPLE_NAME = "example";
    private static final String EXAMPLE_EMAIL = "test@example.com";
    private static final String EXAMPLE_PASS = "pass123";
    private static final String EXAMPLE_DNI = "12345678A";
    private static final String EXAMPLE_LAST_NAME = "last name example";
    private static final String ROL = "client";
    private static final UserRegisterDTO USER_REGISTER_DTO = new UserRegisterDTO(
            EXAMPLE_DNI,
            EXAMPLE_EMAIL,
            EXAMPLE_PASS,
            EXAMPLE_PASS,
            EXAMPLE_NAME,
            EXAMPLE_LAST_NAME
    );
    private static final UserCreateDTO USER_CREATE_DTO = new UserCreateDTO(EXAMPLE_DNI, EXAMPLE_EMAIL, EXAMPLE_NAME, EXAMPLE_LAST_NAME);
    private static final UserDTO USER_DTO = new UserDTO(EXAMPLE_ID,EXAMPLE_NAME, EXAMPLE_EMAIL, EXAMPLE_DNI, EXAMPLE_LAST_NAME,ROL);

    @Test
    void registerSuccessful() {
        UserDTO USER_DTO = new UserDTO();
        USER_DTO.setName(EXAMPLE_NAME);
        USER_DTO.setEmail(EXAMPLE_EMAIL);
        USER_DTO.setDni(EXAMPLE_DNI);

        when(this.clientService.register(USER_REGISTER_DTO)).thenReturn(USER_DTO);

        ResponseEntity<?> result = clientResource.register(USER_REGISTER_DTO);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertTrue(result.getBody() instanceof UserDTO);

        UserDTO resultDto = (UserDTO) result.getBody();
        assertEquals(USER_DTO.getEmail(), resultDto.getEmail());
        assertEquals(USER_DTO.getDni(), resultDto.getDni());
    }

    @Test
    void createClientSuccessful(){
        when(this.clientService.create(USER_CREATE_DTO)).thenReturn(USER_DTO);
        ResponseEntity<?> result = clientResource.create(USER_CREATE_DTO);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertTrue(result.getBody() instanceof UserDTO);
    }

    @Test
    void  deleteClientSuccessful(){
        doNothing().when(clientService).delete(EXAMPLE_ID);
        ResponseEntity<?> result = clientResource.delete(EXAMPLE_ID);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

}
