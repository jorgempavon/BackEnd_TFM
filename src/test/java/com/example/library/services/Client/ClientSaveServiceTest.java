package com.example.library.services.Client;

import com.example.library.entities.dto.UserDTO;
import com.example.library.entities.dto.UserRegisterDTO;
import com.example.library.entities.dto.UserSaveDTO;
import com.example.library.entities.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ClientSaveServiceTest {
    @InjectMocks
    private ClientSaveService clientSaveService;
    private static final String exampleName = "example";
    private static final String exampleLastName = "last name example";
    private static final String exampleEmail = "test@example.com";
    private static final String examplePass = "pass123";
    private static final String exampleEncodedPass = "encodedPass";
    private static final String exampleDni = "12345678A";
    private static final String rol = "client";
    private static final UserSaveDTO userSaveDTO = new UserSaveDTO(
            exampleDni,
            exampleEmail,
            exampleName,
            exampleLastName,
            exampleEncodedPass,
            rol
    );
    private static final UserRegisterDTO userRegisterDTO = new UserRegisterDTO(
            exampleDni,
            exampleEmail,
            examplePass,
            examplePass,
            exampleName,
            exampleLastName
    );
    @Test
    void saveClientUser_successful(){
        UserDTO responseSave = this.clientSaveService.saveClientUser(userSaveDTO);

        assertEquals(responseSave.getEmail(),userSaveDTO.getEmail());
        assertEquals(responseSave.getDni(),userSaveDTO.getDni());
        assertEquals(responseSave.getName(),userSaveDTO.getName());
        assertEquals(responseSave.getLastName(),userSaveDTO.getLastName());
    }
    @Test
    void buildUserSaveDto_successful(){
        UserSaveDTO responseSave = this.clientSaveService.buildUserSaveDto(userRegisterDTO);

        assertEquals(responseSave.getEmail(),userRegisterDTO.getEmail());
        assertEquals(responseSave.getDni(),userRegisterDTO.getDni());
        assertEquals(responseSave.getName(),userRegisterDTO.getName());
        assertEquals(responseSave.getLastName(),userRegisterDTO.getLastName());
    }


}
