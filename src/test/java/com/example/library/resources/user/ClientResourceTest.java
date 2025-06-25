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
    private static final Long exampleId = 2L;
    private static final String exampleName = "example";
    private static final String exampleEmail = "test@example.com";
    private static final String examplePass = "pass123";
    private static final String exampleDni = "12345678A";
    private static final String exampleLastName = "last name example";
    private static final String rol = "client";
    private static final UserRegisterDTO userRegisterDTO = new UserRegisterDTO(
            exampleDni,
            exampleEmail,
            examplePass,
            examplePass,
            exampleName,
            exampleLastName
    );
    private static final UserCreateDTO userCreateDto = new UserCreateDTO(exampleDni, exampleEmail, exampleName, exampleLastName);
    private static final UserDTO userDTO = new UserDTO(exampleId,exampleName, exampleEmail, exampleDni, exampleLastName,rol);

    @Test
    void register_successful() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName(exampleName);
        userDTO.setEmail(exampleEmail);
        userDTO.setDni(exampleDni);

        when(this.clientService.register(userRegisterDTO)).thenReturn(userDTO);

        ResponseEntity<?> result = clientResource.register(userRegisterDTO);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertTrue(result.getBody() instanceof UserDTO);

        UserDTO resultDto = (UserDTO) result.getBody();
        assertEquals(userDTO.getEmail(), resultDto.getEmail());
        assertEquals(userDTO.getDni(), resultDto.getDni());
    }

    @Test
    void createClient_successful(){
        when(this.clientService.create(userCreateDto)).thenReturn(userDTO);
        ResponseEntity<?> result = clientResource.create(userCreateDto);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertTrue(result.getBody() instanceof UserDTO);
    }

    @Test
    void  deleteClient_successful(){
        doNothing().when(clientService).delete(exampleId);
        ResponseEntity<?> result = clientResource.delete(exampleId);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

}
