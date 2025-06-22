package com.example.library.services.Admin;

import com.example.library.api.exceptions.models.BadRequestException;
import com.example.library.config.PasswordService;
import com.example.library.entities.dto.UserCreateDTO;
import com.example.library.entities.dto.UserDTO;
import com.example.library.entities.dto.UserRegisterDTO;
import com.example.library.entities.dto.UserSaveDTO;
import com.example.library.entities.model.Admin;
import com.example.library.entities.model.Client;
import com.example.library.entities.model.User;
import com.example.library.entities.repository.AdminRepository;
import com.example.library.entities.repository.ClientRepository;
import com.example.library.entities.repository.UserRepository;
import com.example.library.services.Client.ClientSaveServiceTest;
import com.example.library.services.Client.ClientService;
import com.example.library.services.EmailService;
import com.example.library.services.User.UserValidatorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {
    @Mock
    private UserValidatorService userValidatorService;
    @Mock
    private EmailService emailService;
    @Mock
    private PasswordService passwordService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AdminRepository adminRepository;
    @InjectMocks
    private AdminService adminService;

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
    void create_createDto_whenExistsEmail_throwsBadRequestException(){
        when(this.userRepository.existsByEmail(exampleEmail)).thenReturn(true);
        when(this.userRepository.existsByDni(exampleDni)).thenReturn(false);
        when(this.userRepository.findByEmail(exampleEmail)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> {
            this.adminService.create(userCreateDTO);
        });
    }
    @Test
    void create_createDto_whenExistsDni_throwsBadRequestException(){
        when(this.userRepository.existsByEmail(exampleEmail)).thenReturn(false);
        when(this.userRepository.existsByDni(exampleDni)).thenReturn(true);
        when(this.userRepository.findByDni(exampleDni)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> {
            this.adminService.create(userCreateDTO);
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
            this.adminService.create(userCreateDTO);
        });
    }

    @Test
    void delete_successful_whenUserNotExists(){
        when(this.userRepository.existsById(exampleId)).thenReturn(false);
        when(this.adminRepository.existsByUserId(exampleId)).thenReturn(false);

        this.adminService.delete(exampleId);
    }

    @Test
    void delete_successful_whenUserIsClient(){
        Admin admin =  new Admin();
        admin.setUser(user);
        when(this.userRepository.existsById(exampleId)).thenReturn(true);
        when(this.userRepository.findById(exampleId)).thenReturn(Optional.of(user));
        when(this.adminRepository.existsByUserId(exampleId)).thenReturn(true);
        when(this.adminRepository.findByUserId(exampleId)).thenReturn(Optional.of(admin));

        this.adminService.delete(exampleId);
    }
}
