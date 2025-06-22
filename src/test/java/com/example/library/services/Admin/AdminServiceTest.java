package com.example.library.services.Admin;

import com.example.library.api.exceptions.models.BadRequestException;
import com.example.library.config.PasswordService;
import com.example.library.entities.dto.UserCreateDTO;
import com.example.library.entities.dto.UserDTO;
import com.example.library.entities.dto.UserSaveDTO;
import com.example.library.entities.model.Admin;
import com.example.library.entities.model.User;
import com.example.library.entities.repository.AdminRepository;
import com.example.library.entities.repository.UserRepository;
import com.example.library.services.EmailService;
import com.example.library.services.User.UserValidatorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
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
    private static final String rol = "admin";
    private final String status = "status";
    private final String message = "message";
    private final String statusEmail = "statusEmail";
    private final String statusDni = "statusDni";
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
        when(this.passwordService.generateStrongPassword()).thenReturn(examplePass);

        Map<String, Object> responseExistsUser = new HashMap<>();
        responseExistsUser.put(status,false);
        responseExistsUser.put(statusEmail,false);
        responseExistsUser.put(statusDni,false);
        responseExistsUser.put(message,"");

        when(this.userValidatorService.checkUserExistence(exampleEmail,exampleDni)).thenReturn(responseExistsUser);
        when(this.userValidatorService.buildUserSaveDto(userCreateDTO,examplePass,rol)).thenReturn(userSaveDTO);

        UserDTO response = this.adminService.create(userCreateDTO);
        assertNotNull(response);
        assertEquals(exampleName,response.getName());
        assertEquals(exampleEmail,response.getEmail());
        assertEquals(exampleLastName,response.getLastName());
        assertEquals(rol,response.getRol());
    }

    @Test
    void create_createDto_whenExistsEmail_throwsBadRequestException(){
        Map<String, Object> responseExistsUser = new HashMap<>();
        responseExistsUser.put(status,true);
        responseExistsUser.put(statusEmail,true);
        responseExistsUser.put(statusDni,false);
        responseExistsUser.put(message,"");

        when(this.userValidatorService.checkUserExistence(exampleEmail,exampleDni)).thenReturn(responseExistsUser);
        assertThrows(BadRequestException.class, () -> {
            this.adminService.create(userCreateDTO);
        });
    }
    @Test
    void create_createDto_whenExistsDni_throwsBadRequestException(){
        Map<String, Object> responseExistsUser = new HashMap<>();
        responseExistsUser.put(status,true);
        responseExistsUser.put(statusEmail,false);
        responseExistsUser.put(statusDni,true);
        responseExistsUser.put(message,"");

        when(this.userValidatorService.checkUserExistence(exampleEmail,exampleDni)).thenReturn(responseExistsUser);

        assertThrows(BadRequestException.class, () -> {
            this.adminService.create(userCreateDTO);
        });
    }

    @Test
    void create_createDto_whenIsNotValidEmail_throwsBadRequestException(){
        Map<String, Object> responseExistsUser = new HashMap<>();
        responseExistsUser.put(status,false);
        responseExistsUser.put(statusEmail,false);
        responseExistsUser.put(statusDni,false);

        responseExistsUser.put(message,"");
        when(this.passwordService.generateStrongPassword()).thenReturn(examplePass);
        when(this.userValidatorService.checkUserExistence(exampleEmail,exampleDni)).thenReturn(responseExistsUser);
        when(this.userValidatorService.buildUserSaveDto(userCreateDTO,examplePass,rol)).thenReturn(userSaveDTO);


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
