package com.example.library.services.Client;

import com.example.library.api.exceptions.models.BadRequestException;
import com.example.library.api.exceptions.models.ConflictException;
import com.example.library.config.PasswordService;
import com.example.library.entities.dto.UserCreateDTO;
import com.example.library.entities.dto.UserDTO;
import com.example.library.entities.dto.UserRegisterDTO;
import com.example.library.entities.dto.UserSaveDTO;
import com.example.library.entities.model.Client;
import com.example.library.entities.model.User;
import com.example.library.entities.repository.ClientRepository;
import com.example.library.entities.repository.UserRepository;
import com.example.library.services.EmailService;
import com.example.library.services.User.UserValidatorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {
    @Mock
    private  UserValidatorService userValidatorService;
    @Mock
    private  EmailService emailService;
    @Mock
    private  PasswordService passwordService;
    @Mock
    private  UserRepository userRepository;
    @Mock
    private ClientSaveServiceTest clientSaveServiceTest;
    @Mock
    private  ClientRepository clientRepository;
    @InjectMocks
    private ClientService clientService;

    private static final Long exampleId = 2L;
    private static final String exampleName = "example";
    private static final String exampleLastName = "last name example";
    private static final String exampleEmail = "test@example.com";
    private static final String examplePass = "pass123";
    private static final String exampleEncodedPass = "encodedPass";
    private static final String exampleDni = "12345678A";
    private static final String rol = "client";

    private static final User user = new User(
            exampleName,
            exampleDni,
            exampleEmail,
            exampleLastName
    );
    private static final UserRegisterDTO userRegisterDTO = new UserRegisterDTO(
            exampleDni,
            exampleEmail,
            examplePass,
            examplePass,
            exampleName,
            exampleLastName
    );
    private static final UserCreateDTO userCreateDTO = new UserCreateDTO(
            exampleDni,
            exampleEmail,
            exampleName,
            exampleLastName
    );

    private static final UserSaveDTO userSaveDTO = new UserSaveDTO(
            exampleDni,
            exampleEmail,
            exampleName,
            exampleLastName,
            exampleEncodedPass,
            rol
    );

    @Test
    void create_registerDto_successful(){
        when(this.userRepository.existsByEmail(exampleEmail)).thenReturn(false);
        when(this.userRepository.existsByDni(exampleDni)).thenReturn(false);
        when(this.passwordService.encodePasswords(examplePass)).thenReturn(exampleEncodedPass);
        when(this.clientSaveServiceTest.buildUserSaveDto(userRegisterDTO)).thenReturn(userSaveDTO);

        UserDTO response = this.clientService.register(userRegisterDTO);
        assertNotNull(response);
        assertEquals(exampleName,response.getName());
        assertEquals(exampleEmail,response.getEmail());
        assertEquals(exampleLastName,response.getLastName());
        assertEquals(false,response.getIsAdmin());
    }

    @Test
    void create_createDto_successful(){
        when(this.userRepository.existsByEmail(exampleEmail)).thenReturn(false);
        when(this.userRepository.existsByDni(exampleDni)).thenReturn(false);
        when(this.passwordService.generateStrongPassword()).thenReturn(examplePass);
        when(this.passwordService.encodePasswords(examplePass)).thenReturn(exampleEncodedPass);
        when(this.userValidatorService.buildUserSaveDto(userCreateDTO,examplePass,rol)).thenReturn(userSaveDTO);

        UserDTO response = this.clientService.create(userCreateDTO);
        assertNotNull(response);
        assertEquals(exampleName,response.getName());
        assertEquals(exampleEmail,response.getEmail());
        assertEquals(exampleLastName,response.getLastName());
        assertEquals(rol,response.getRol());
    }

    @Test
    void create_registerDto_whenExistsUserEmail_throwsBadRequestException(){
        when(this.userRepository.existsByEmail(exampleEmail)).thenReturn(true);
        when(this.userRepository.existsByDni(exampleDni)).thenReturn(false);
        when(this.userRepository.findByEmail(exampleEmail)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> {
            this.clientService.register(userRegisterDTO);
        });
    }
    @Test
    void create_registerDto_whenExistsUserDni_throwsBadRequestException(){
        when(this.userRepository.existsByEmail(exampleEmail)).thenReturn(false);
        when(this.userRepository.existsByDni(exampleDni)).thenReturn(true);
        when(this.userRepository.findByDni(exampleDni)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> {
            this.clientService.register(userRegisterDTO);
        });
    }

    @Test
    void create_createDto_whenExistsEmail_throwsBadRequestException(){
        when(this.userRepository.existsByEmail(exampleEmail)).thenReturn(true);
        when(this.userRepository.existsByDni(exampleDni)).thenReturn(false);
        when(this.userRepository.findByEmail(exampleEmail)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> {
            this.clientService.create(userCreateDTO);
        });
    }
    @Test
    void create_createDto_whenExistsDni_throwsBadRequestException(){
        when(this.userRepository.existsByEmail(exampleEmail)).thenReturn(false);
        when(this.userRepository.existsByDni(exampleDni)).thenReturn(true);
        when(this.userRepository.findByDni(exampleDni)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> {
            this.clientService.create(userCreateDTO);
        });
    }

    @Test
    void create_createDto_whenIsNotValidEmail_throwsBadRequestException(){
        when(this.userRepository.existsByEmail(exampleEmail)).thenReturn(false);
        when(this.userRepository.existsByDni(exampleDni)).thenReturn(false);
        when(this.passwordService.generateStrongPassword()).thenReturn(examplePass);
        when(this.passwordService.encodePasswords(examplePass)).thenReturn(exampleEncodedPass);

        doThrow(new BadRequestException("El correo proporcionado no existe"))
                .when(emailService)
                .newAccountEmail(any(String.class), any(String.class), any(String.class));

        assertThrows(BadRequestException.class, () -> {
            this.clientService.create(userCreateDTO);
        });
    }

    @Test
    void create_registerDto_whenIsNotValidEmail_throwsBadRequestException(){
        when(this.userRepository.existsByEmail(exampleEmail)).thenReturn(false);
        when(this.userRepository.existsByDni(exampleDni)).thenReturn(false);
        when(this.passwordService.encodePasswords(examplePass)).thenReturn(exampleEncodedPass);

        doThrow(new BadRequestException("El correo proporcionado no existe"))
                .when(emailService)
                .newAccountEmail(any(String.class), any(String.class), any(String.class));

        assertThrows(BadRequestException.class, () -> {
            this.clientService.register(userRegisterDTO);
        });
    }
    @Test
    void delete_successful_whenUserNotExists(){
        when(this.userRepository.existsById(exampleId)).thenReturn(false);
        when(this.clientRepository.existsByUserId(exampleId)).thenReturn(false);

        this.clientService.delete(exampleId);
    }

    @Test
    void delete_successful_whenUserIsClient(){
        Client client =  new Client();
        client.setUser(user);
        when(this.userRepository.existsById(exampleId)).thenReturn(true);
        when(this.userRepository.findById(exampleId)).thenReturn(Optional.of(user));
        when(this.clientRepository.existsByUserId(exampleId)).thenReturn(true);
        when(this.clientRepository.findByUserId(exampleId)).thenReturn(Optional.of(client));

        this.clientService.delete(exampleId);
    }

    @Test
    void register_successful() {
        UserDTO userDTO  = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail(exampleEmail);
        userDTO.setName(exampleName);
        userDTO.setLastName(exampleLastName);
        userDTO.setDni(exampleDni);
        userDTO.setIsAdmin(false);

        when(this.clientService.register(any(UserRegisterDTO.class)))
                .thenReturn(userDTO);

        UserDTO result = clientService.register(userRegisterDTO);
        assertNotNull(result);
        assertEquals(userRegisterDTO.getDni(), result.getDni());
        assertEquals(userRegisterDTO.getEmail(), result.getEmail());
        assertEquals(userRegisterDTO.getName(), result.getName());
        assertEquals(userRegisterDTO.getLastName(), result.getLastName());
    }

    @Test
    void register_whenPasswordsDoNotMatch_throwsConflictException() {
        UserRegisterDTO newUserRegisterDto = new UserRegisterDTO();
        newUserRegisterDto.setName(exampleName);
        newUserRegisterDto.setName(exampleLastName);
        newUserRegisterDto.setEmail(exampleEmail);
        newUserRegisterDto.setDni(exampleDni);
        newUserRegisterDto.setPassword(examplePass);
        String exampleBadPass = "pass";
        newUserRegisterDto.setRepeatPassword(exampleBadPass);

        assertThrows(ConflictException.class, () -> {
            clientService.register(newUserRegisterDto);
        });
    }

    @Test
    void register_whenUserExists_throwsBadRequestException() {
        when(this.userService.create(userRegisterDTO))
                .thenThrow(BadRequestException.class);

        assertThrows(BadRequestException.class, () -> {
            clientService.register(userRegisterDTO);
        });
    }

}
