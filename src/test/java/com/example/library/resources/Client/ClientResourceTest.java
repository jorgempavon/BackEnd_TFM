package com.example.library.resources.Client;

import com.example.library.api.exceptions.models.BadRequestException;
import com.example.library.api.exceptions.models.ConflictException;
import com.example.library.api.resources.ClientResource;
import com.example.library.entities.dto.UserCreateDTO;
import com.example.library.entities.dto.UserDTO;
import com.example.library.entities.dto.UserRegisterDTO;
import com.example.library.services.Client.ClientService;
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
    void register_whenPasswordsDoNotMatch_throwsConflictException() {
        when(this.clientService.register(userRegisterDTO))
                .thenThrow(new ConflictException("Las contraseñas proporcionadas no coinciden"));

        assertThrows(ConflictException.class, () -> {
            clientResource.register(userRegisterDTO);
        });
    }
    @Test
    void register_whenDniExists_throwsBadRequestException() {
        when(this.clientService.register(userRegisterDTO))
                .thenThrow(new BadRequestException("El Dni proporcionado pertenece a otro usuario. Por favor, inténtelo de nuevo"));

        assertThrows(BadRequestException.class, () -> {
            clientResource.register(userRegisterDTO);
        });
    }
    @Test
    void register_whenEmailExists_throwsBadRequestException() {
        when(this.clientService.register(userRegisterDTO))
                .thenThrow(new BadRequestException("El Email proporcionado pertenece a otro usuario. Por favor, inténtelo de nuevo"));

        assertThrows(BadRequestException.class, () -> {
            clientResource.register(userRegisterDTO);
        });
    }
    @Test
    void register_whenEmptyDto_throwsException() {
        UserRegisterDTO newEmptyUserRegisterDTO = new UserRegisterDTO();

        assertThrows(Exception.class, () -> {
            clientResource.register(newEmptyUserRegisterDTO);
        });
    }
    @Test
    void createClient_successful(){
        when(this.clientService.create(userCreateDto)).thenReturn(userDTO);
        ResponseEntity<?> result = clientResource.create(userCreateDto);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertTrue(result.getBody() instanceof UserDTO);
    }
    @Test
    void createClient_whenUserExists_throwsBadRequestException(){
        when(this.clientService.create(userCreateDto))
                .thenThrow(new BadRequestException("El dni o email proporcionados pertenecen a otro usuario"));
        assertThrows(BadRequestException.class, () -> {
            clientResource.create(userCreateDto);
        });
    }
    @Test
    void createClient_whenEmailNotExists_throwsBadRequestException(){
        assertThrows(BadRequestException.class, () -> {
            when(this.clientService.create(userCreateDto))
                    .thenThrow(new BadRequestException("El email proporcionado no existe"));
            clientResource.create(userCreateDto);
        });
    }
    @Test
    void  deleteClient_successful(){
        doNothing().when(clientService).delete(exampleId);
        ResponseEntity<?> result = clientResource.delete(exampleId);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

}
