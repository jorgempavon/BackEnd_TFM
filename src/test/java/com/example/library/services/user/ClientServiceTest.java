package com.example.library.services.user;

import com.example.library.api.exceptions.models.ConflictException;
import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.entities.dto.user.*;
import com.example.library.entities.model.user.Admin;
import com.example.library.entities.model.user.Client;
import com.example.library.entities.model.user.User;
import com.example.library.entities.repository.user.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {
    @Mock
    private  ClientRepository clientRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private ClientService clientService;
    private static final Long EXAMPLE_ID = 2L;

    private static final Long USER_ID = 2L;
    private static final Long CLIENT_ID = 24L;
    private static final String EXAMPLE_NAME = "example";
    private static final String EXAMPLE_LAST_NAME = "last name example";
    private static final String EXAMPLE_EMAIL = "test@example.com";
    private static final String EXAMPLE_DNI = "12345678A";
    private static final String EXAMPLE_PASS = "pass123";
    private static final String FULL_NAME = "User full name";

    private static final User USER = new User(
            USER_ID,
            EXAMPLE_NAME,
            EXAMPLE_DNI,
            EXAMPLE_EMAIL,
            EXAMPLE_LAST_NAME
    );
    private static final String ROL = "client";
    private static final UserDTO USER_DTO = new UserDTO(
            EXAMPLE_ID,
            EXAMPLE_NAME,
            EXAMPLE_EMAIL,
            EXAMPLE_DNI,
            EXAMPLE_LAST_NAME,
            ROL
    );
    private static final UserAndUserDTO USER_AND_USER_DTO = new UserAndUserDTO(
            USER,USER_DTO
    );
    private static final Client CLIENT = new Client(
            CLIENT_ID,USER
    );
    private static final UserCreateDTO USER_CREATE_DTO = new UserCreateDTO(
            EXAMPLE_DNI,
            EXAMPLE_EMAIL,
            EXAMPLE_NAME,
            EXAMPLE_LAST_NAME
    );

    @Test
    void createClient_createDto_successful(){
        when(this.userService.create(USER_CREATE_DTO,ROL,"")).thenReturn(USER_AND_USER_DTO);

        UserDTO response = this.clientService.create(USER_CREATE_DTO);
        assertNotNull(response);
        assertEquals(EXAMPLE_NAME,response.getName());
        assertEquals(EXAMPLE_EMAIL,response.getEmail());
        assertEquals(EXAMPLE_LAST_NAME,response.getLastName());
        assertEquals(ROL,response.getRol());
    }

    @Test
    void deleteClient_successful_whenUserNotExists(){
        when(this.clientRepository.existsByUserId(EXAMPLE_ID)).thenReturn(false);
        doNothing().when(this.userService).delete(EXAMPLE_ID);
        this.clientService.delete(EXAMPLE_ID);
    }

    @Test
    void deleteClient_successful_whenUserIsClient(){
        Client client =  new Client();
        client.setUser(USER);
        when(this.clientRepository.existsByUserId(EXAMPLE_ID)).thenReturn(true);
        when(this.clientRepository.findByUserId(EXAMPLE_ID)).thenReturn(Optional.of(client));
        doNothing().when(this.userService).delete(EXAMPLE_ID);

        this.clientService.delete(EXAMPLE_ID);
    }
    @Test
    void registerDto_successful(){
        UserRegisterDTO USER_REGISTER_DTO = new UserRegisterDTO(
                EXAMPLE_DNI,EXAMPLE_EMAIL,EXAMPLE_PASS,EXAMPLE_PASS,
                EXAMPLE_NAME,EXAMPLE_LAST_NAME
        );
        when(this.userService.create(any(UserCreateDTO.class),any(String.class),any(String.class)))
                .thenReturn(USER_AND_USER_DTO);

        UserDTO response = this.clientService.register(USER_REGISTER_DTO);
        assertNotNull(response);
        assertEquals(EXAMPLE_NAME,response.getName());
        assertEquals(EXAMPLE_EMAIL,response.getEmail());
        assertEquals(EXAMPLE_LAST_NAME,response.getLastName());
        assertEquals(ROL,response.getRol());
    }

    @Test
    void register_whenPasswordsDoNotMatch_throwsConflictException() {
        UserRegisterDTO newUserRegisterDto = new UserRegisterDTO();
        newUserRegisterDto.setName(EXAMPLE_NAME);
        newUserRegisterDto.setName(EXAMPLE_LAST_NAME);
        newUserRegisterDto.setEmail(EXAMPLE_EMAIL);
        newUserRegisterDto.setDni(EXAMPLE_DNI);
        newUserRegisterDto.setPassword(EXAMPLE_PASS);
        String exampleBadPass = "pass";
        newUserRegisterDto.setRepeatPassword(exampleBadPass);

        assertThrows(ConflictException.class, () -> {
            clientService.register(newUserRegisterDto);
        });
    }
    @Test
    void getUserFullNameByClient_successful(){
        when(this.clientRepository.existsById(CLIENT_ID)).thenReturn(true);
        when(this.userService.getUserFullName(USER)).thenReturn(FULL_NAME);

        String responseFullName =  this.clientService.getUserFullNameByClient(CLIENT);

        assertEquals(responseFullName,FULL_NAME);
    }
    @Test
    void getUserFullNameByClient_NotExistsClient_throwNotFoundException(){
        when(this.clientRepository.existsById(CLIENT_ID)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> {
            clientService.getUserFullNameByClient(CLIENT);
        });
    }

    @Test
    void getClientIdByUserId_successful(){
        when(this.clientRepository.existsByUserId(USER_ID)).thenReturn(true);
        when(this.clientRepository.findByUserId(USER_ID)).thenReturn(Optional.of(CLIENT));

        Long responseClient =  this.clientService.getClientIdByUserId(USER_ID);

        assertEquals(responseClient,CLIENT_ID);
    }
    @Test
    void getClientIdByUserId_NotExistsClient_throwNotFoundException(){
        when(this.clientRepository.existsByUserId(USER_ID)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> {
            clientService.getClientIdByUserId(USER_ID);
        });
    }

    @Test
    void isClientEqualsByUserIdAndClient_Equals(){
        when(this.userService.existsById(USER_ID)).thenReturn(true);
        when(this.clientRepository.existsByUserId(USER_ID)).thenReturn(true);
        when(this.clientRepository.existsById(CLIENT_ID)).thenReturn(true);
        when(this.clientRepository.findByUserId(USER_ID)).thenReturn(Optional.of(CLIENT));

        Boolean response =  this.clientService.isClientEqualsByUserIdAndClient(CLIENT,USER_ID);
        assertTrue(response);
    }
    @Test
    void isClientEqualsByUserIdAndClient_NotFound(){
        Long otherUserId = 72L;
        when(this.userService.existsById(otherUserId)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> {
            this.clientService.isClientEqualsByUserIdAndClient(CLIENT,otherUserId);
        });
    }
    @Test
    void isClientEqualsByUserIdAndClient_NotEquals(){
        Long otherUserId = 72L;
        long otherClientId = 12L;
        Client otherClient = new Client(12L,USER);
        when(this.userService.existsById(otherUserId)).thenReturn(true);
        when(this.clientRepository.existsByUserId(otherUserId)).thenReturn(true);
        when(this.clientRepository.existsById(otherClientId)).thenReturn(true);
        when(this.clientRepository.findByUserId(otherUserId)).thenReturn(Optional.of(CLIENT));

        Boolean response =  this.clientService.isClientEqualsByUserIdAndClient(otherClient,otherUserId);
        assertFalse(response);
    }
    @Test
    void isClientEqualsByUserIdAndClient_NotExistsUserLogged(){
        when(this.userService.existsById(USER_ID)).thenReturn(true);
        when(this.clientRepository.existsByUserId(USER_ID)).thenReturn(false);

        Boolean response =  this.clientService.isClientEqualsByUserIdAndClient(CLIENT,USER_ID);
        assertFalse(response);
    }
    @Test
    void isClientEqualsByUserIdAndClient_NotExistsClientProvided(){
        when(this.userService.existsById(USER_ID)).thenReturn(true);
        when(this.clientRepository.existsByUserId(USER_ID)).thenReturn(true);
        when(this.clientRepository.existsById(CLIENT_ID)).thenReturn(false);

        Boolean response =  this.clientService.isClientEqualsByUserIdAndClient(CLIENT,USER_ID);
        assertFalse(response);
    }

    @Test
    void getUserEmailByClient_successful(){
        when(this.clientRepository.existsById(CLIENT_ID)).thenReturn(true);
        when(this.userService.getUserEmail(USER)).thenReturn(EXAMPLE_EMAIL);

        String responseClient =  this.clientService.getUserEmailByClient(CLIENT);

        assertEquals(responseClient,EXAMPLE_EMAIL);
    }
    @Test
    void getUserEmailByClient_NotExistsClient_throwNotFoundException(){
        when(this.clientRepository.existsById(CLIENT_ID)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> {
            this.clientService.getUserEmailByClient(CLIENT);
        });
    }

    @Test
    void getClientByUserId_successful(){
        when(this.clientRepository.existsByUserId(USER_ID)).thenReturn(true);
        when(this.clientRepository.findByUserId(USER_ID)).thenReturn(Optional.of(CLIENT));
        Client responseClient =  this.clientService.getClientByUserId(USER_ID);

        assertEquals(responseClient,CLIENT);
    }
    @Test
    void getClientByUserId_NotExistsClient_throwNotFoundException(){
        when(this.clientRepository.existsByUserId(USER_ID)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> {
            clientService.getClientByUserId(USER_ID);
        });
    }
}
