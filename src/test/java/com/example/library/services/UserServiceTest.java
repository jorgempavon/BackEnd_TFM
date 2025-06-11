package com.example.library.services;

import com.example.library.api.exceptions.models.BadRequestException;
import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.entities.dto.UserCreateDTO;
import com.example.library.entities.dto.UserDTO;
import com.example.library.entities.dto.UserRegisterDTO;
import com.example.library.entities.model.User;
import com.example.library.entities.repository.AdminRepository;
import com.example.library.entities.repository.ClientRepository;
import com.example.library.entities.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private JavaMailSender mailSender;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private PasswordGenerator passwordGenerator;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AdminRepository adminRepository;
    @Mock
    private ClientRepository clientRepository;
    @InjectMocks
    private UserService userService;

    private final Long EXAMPLE_ID = 2L;
    private final String EXAMPLE_NAME = "example";
    private final String EXAMPLE_LAST_NAME = "last name example";
    private final String EXAMPLE_EMAIL = "test@example.com";
    private final String EXAMPLE_PASSWORD = "pass123";

    private final String EXAMPLE_ENCODED_PASSWORD = "encodedPass";
    private final String EXAMPLE_DNI = "12345678A";

    private final UserRegisterDTO userRegisterDTO = new UserRegisterDTO(
            EXAMPLE_DNI,
            EXAMPLE_EMAIL,
            EXAMPLE_PASSWORD,
            EXAMPLE_PASSWORD,
            EXAMPLE_NAME,
            EXAMPLE_LAST_NAME
    );

    private final UserCreateDTO userCreateDTO = new UserCreateDTO(
            EXAMPLE_DNI,
            EXAMPLE_EMAIL,
            EXAMPLE_NAME,
            EXAMPLE_LAST_NAME,
            true
    );

    @Test
    void findById_successful(){
        User user = new User("example", "test@example.com", "12345678A", "Last example");
        UserDTO userDTO = user.getUserDTO(false);
        when(this.userRepository.existsById(EXAMPLE_ID)).thenReturn(true);
        when(this.adminRepository.existsByUserId(EXAMPLE_ID)).thenReturn(false);
        when(userRepository.findById(EXAMPLE_ID)).thenReturn(Optional.of(user));
        UserDTO result = this.userService.findById(EXAMPLE_ID);

        assertEquals(userDTO.getDni(), result.getDni());
        assertEquals(userDTO.getEmail(), result.getEmail());
        assertEquals(userDTO.getName(), result.getName());
        assertEquals(userDTO.getLastName(), result.getLastName());
    }

    @Test
    void findById_whenNotExistsId_throwsNotFoundException(){
        when(this.userRepository.existsById(EXAMPLE_ID)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> {
            userService.findById(EXAMPLE_ID);
        });
    }

    @Test
    void create_registerDto_successful(){
        when(this.userRepository.existsByEmail(EXAMPLE_EMAIL)).thenReturn(false);
        when(this.userRepository.existsByDni(EXAMPLE_DNI)).thenReturn(false);
        when(this.passwordEncoder.encode(EXAMPLE_PASSWORD)).thenReturn(EXAMPLE_ENCODED_PASSWORD);

        UserDTO response = this.userService.create(userRegisterDTO);
        assertNotNull(response);
        assertEquals(EXAMPLE_NAME,response.getName());
        assertEquals(EXAMPLE_EMAIL,response.getEmail());
        assertEquals(EXAMPLE_LAST_NAME,response.getLastName());
        assertEquals(false,response.getIsAdmin());
    }

    @Test
    void create_createDto_successful(){
        when(this.userRepository.existsByEmail(EXAMPLE_EMAIL)).thenReturn(false);
        when(this.userRepository.existsByDni(EXAMPLE_DNI)).thenReturn(false);
        when(this.passwordGenerator.generateStrongPassword()).thenReturn(EXAMPLE_PASSWORD);
        when(this.passwordEncoder.encode(EXAMPLE_PASSWORD)).thenReturn(EXAMPLE_ENCODED_PASSWORD);

        UserDTO response = this.userService.create(userCreateDTO);
        assertNotNull(response);
        assertEquals(EXAMPLE_NAME,response.getName());
        assertEquals(EXAMPLE_EMAIL,response.getEmail());
        assertEquals(EXAMPLE_LAST_NAME,response.getLastName());
        assertEquals(true,response.getIsAdmin());
    }

    @Test
    void create_registerDto_whenExistsUserEmail_throwsBadRequestException(){
        when(this.userRepository.existsByEmail(EXAMPLE_EMAIL)).thenReturn(true);
        when(this.userRepository.existsByDni(EXAMPLE_DNI)).thenReturn(false);

        assertThrows(BadRequestException.class, () -> {
            this.userService.create(userRegisterDTO);
        });
    }
    @Test
    void create_registerDto_whenExistsUserDni_throwsBadRequestException(){
        when(this.userRepository.existsByEmail(EXAMPLE_EMAIL)).thenReturn(false);
        when(this.userRepository.existsByDni(EXAMPLE_DNI)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> {
            this.userService.create(userRegisterDTO);
        });
    }

    @Test
    void create_createDto_whenExistsEmail_throwsBadRequestException(){
        when(this.userRepository.existsByEmail(EXAMPLE_EMAIL)).thenReturn(true);
        when(this.userRepository.existsByDni(EXAMPLE_DNI)).thenReturn(false);

        assertThrows(BadRequestException.class, () -> {
            this.userService.create(userCreateDTO);
        });
    }
    @Test
    void create_createDto_whenExistsDni_throwsBadRequestException(){
        when(this.userRepository.existsByEmail(EXAMPLE_EMAIL)).thenReturn(false);
        when(this.userRepository.existsByDni(EXAMPLE_DNI)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> {
            this.userService.create(userCreateDTO);
        });
    }

    @Test
    void create_createDto_whenIsNotValidEmail_throwsBadRequestException(){
        when(this.userRepository.existsByEmail(EXAMPLE_EMAIL)).thenReturn(false);
        when(this.userRepository.existsByDni(EXAMPLE_DNI)).thenReturn(false);
        when(this.passwordGenerator.generateStrongPassword()).thenReturn(EXAMPLE_PASSWORD);
        when(this.passwordEncoder.encode(EXAMPLE_PASSWORD)).thenReturn(EXAMPLE_ENCODED_PASSWORD);

        doThrow(new BadRequestException("El correo proporcionado no es válido"))
                .when(mailSender)
                .send(any(SimpleMailMessage.class));

        assertThrows(BadRequestException.class, () -> {
            this.userService.create(userCreateDTO);
        });
    }

    @Test
    void create_regiterDto_whenIsNotValidEmail_throwsBadRequestException(){
        when(this.userRepository.existsByEmail(EXAMPLE_EMAIL)).thenReturn(false);
        when(this.userRepository.existsByDni(EXAMPLE_DNI)).thenReturn(false);
        when(this.passwordEncoder.encode(EXAMPLE_PASSWORD)).thenReturn(EXAMPLE_ENCODED_PASSWORD)
        ;
        doThrow(new BadRequestException("El correo proporcionado no es válido"))
                .when(mailSender)
                .send(any(SimpleMailMessage.class));

        assertThrows(BadRequestException.class, () -> {
            this.userService.create(userRegisterDTO);
        });
    }
}
