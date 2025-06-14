package com.example.library.services;

import com.example.library.api.exceptions.models.BadRequestException;
import com.example.library.api.exceptions.models.ConflictException;
import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.entities.dto.*;
import com.example.library.entities.model.Admin;
import com.example.library.entities.model.Client;
import com.example.library.entities.model.User;
import com.example.library.entities.repository.AdminRepository;
import com.example.library.entities.repository.ClientRepository;
import com.example.library.entities.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private EmailService emailService;
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
    private final String EXAMPLE_OTHER_PASSWORD = "OtherPass123";
    private final String EXAMPLE_ENCODED_PASSWORD = "encodedPass";
    private final String EXAMPLE_DNI = "12345678A";
    private final boolean EXAMPLE_NOT_ADMIN = false;
    private final boolean EXAMPLE_IS_ADMIN = true;
    private final String EXAMPLE_OTHER_EMAIL = "other@example.com";
    private final String EXAMPLE_OTHER_NAME = "Other Name";
    private final String EXAMPLE_OTHER_LAST_NAME = "Other Last Name";
    private final String EXAMPLE_OTHER_DNI = "23345452F";

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
    private final User user = new User(
            EXAMPLE_NAME,
            EXAMPLE_DNI,
            EXAMPLE_EMAIL,
            EXAMPLE_LAST_NAME
    );

    @Test
    void findById_successful(){
        User user = new User("example", "test@example.com", "12345678A", "Last example");
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setDni(user.getDni());
        userDTO.setEmail(user.getEmail());
        userDTO.setName(user.getName());
        userDTO.setLastName(user.getLastName());
        userDTO.setIsAdmin(false);

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
        when(this.userRepository.findByEmail(EXAMPLE_EMAIL)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> {
            this.userService.create(userRegisterDTO);
        });
    }
    @Test
    void create_registerDto_whenExistsUserDni_throwsBadRequestException(){
        when(this.userRepository.existsByEmail(EXAMPLE_EMAIL)).thenReturn(false);
        when(this.userRepository.existsByDni(EXAMPLE_DNI)).thenReturn(true);
        when(this.userRepository.findByDni(EXAMPLE_DNI)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> {
            this.userService.create(userRegisterDTO);
        });
    }

    @Test
    void create_createDto_whenExistsEmail_throwsBadRequestException(){
        when(this.userRepository.existsByEmail(EXAMPLE_EMAIL)).thenReturn(true);
        when(this.userRepository.existsByDni(EXAMPLE_DNI)).thenReturn(false);
        when(this.userRepository.findByEmail(EXAMPLE_EMAIL)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> {
            this.userService.create(userCreateDTO);
        });
    }
    @Test
    void create_createDto_whenExistsDni_throwsBadRequestException(){
        when(this.userRepository.existsByEmail(EXAMPLE_EMAIL)).thenReturn(false);
        when(this.userRepository.existsByDni(EXAMPLE_DNI)).thenReturn(true);
        when(this.userRepository.findByDni(EXAMPLE_DNI)).thenReturn(Optional.of(user));

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

        doThrow(new BadRequestException("El correo proporcionado no existe"))
                .when(emailService)
                .newAccountEmail(any(String.class), any(String.class), any(String.class));

        assertThrows(BadRequestException.class, () -> {
            this.userService.create(userCreateDTO);
        });
    }

    @Test
    void create_registerDto_whenIsNotValidEmail_throwsBadRequestException(){
        when(this.userRepository.existsByEmail(EXAMPLE_EMAIL)).thenReturn(false);
        when(this.userRepository.existsByDni(EXAMPLE_DNI)).thenReturn(false);
        when(this.passwordEncoder.encode(EXAMPLE_PASSWORD)).thenReturn(EXAMPLE_ENCODED_PASSWORD);

        doThrow(new BadRequestException("El correo proporcionado no existe"))
                .when(emailService)
                .newAccountEmail(any(String.class), any(String.class), any(String.class));

        assertThrows(BadRequestException.class, () -> {
            this.userService.create(userRegisterDTO);
        });
    }

    @Test
    void delete_successful_whenUserNotExists(){
        when(this.userRepository.existsById(EXAMPLE_ID)).thenReturn(false);
        when(this.clientRepository.existsByUserId(EXAMPLE_ID)).thenReturn(false);
        when(this.adminRepository.existsByUserId(EXAMPLE_ID)).thenReturn(false);

        this.userService.delete(EXAMPLE_ID);
    }

    @Test
    void delete_successful_whenUserIsClient(){
        Client client =  new Client();
        client.setUser(user);
        when(this.userRepository.existsById(EXAMPLE_ID)).thenReturn(true);
        when(this.userRepository.findById(EXAMPLE_ID)).thenReturn(Optional.of(user));
        when(this.clientRepository.existsByUserId(EXAMPLE_ID)).thenReturn(true);
        when(this.clientRepository.findByUserId(EXAMPLE_ID)).thenReturn(Optional.of(client));

        this.userService.delete(EXAMPLE_ID);
    }

    @Test
    void delete_successful_whenUserIsAdmin(){
        Admin admin =  new Admin();
        admin.setUser(user);
        when(this.userRepository.existsById(EXAMPLE_ID)).thenReturn(true);
        when(this.userRepository.findById(EXAMPLE_ID)).thenReturn(Optional.of(user));
        when(this.adminRepository.findByUserId(EXAMPLE_ID)).thenReturn(Optional.of(admin));
        when(this.adminRepository.existsByUserId(EXAMPLE_ID)).thenReturn(true);

        this.userService.delete(EXAMPLE_ID);
    }

    @Test
    void findByNameAndDniAndEmail_successful_parametersAreEmpty(){
        List<User> mockUsers = List.of(user);

        when(userRepository.findAll(any(Specification.class))).thenReturn(mockUsers);

        List<UserDTO> result = userService.findByNameAndDniAndEmail("", "", "");

        assertEquals(1, result.size());
        assertEquals(EXAMPLE_NAME, result.get(0).getName());
        assertEquals(EXAMPLE_DNI, result.get(0).getDni());
        assertEquals(EXAMPLE_EMAIL, result.get(0).getEmail());
    }

    @Test
    void findByNameAndDniAndEmail_successful_parametersAreNull(){
        List<User> mockUsers = List.of(user);

        when(userRepository.findAll(any(Specification.class))).thenReturn(mockUsers);

        List<UserDTO> result = userService.findByNameAndDniAndEmail(null, null, null);

        assertEquals(1, result.size());
        assertEquals(EXAMPLE_NAME, result.get(0).getName());
        assertEquals(EXAMPLE_DNI, result.get(0).getDni());
        assertEquals(EXAMPLE_EMAIL, result.get(0).getEmail());
    }

    @Test
    void findByNameAndDniAndEmail_successful(){
        List<User> mockUsers = List.of(user);

        when(userRepository.findAll(any(Specification.class))).thenReturn(mockUsers);

        List<UserDTO> result = userService.findByNameAndDniAndEmail(EXAMPLE_NAME, EXAMPLE_DNI,EXAMPLE_EMAIL);

        assertEquals(1, result.size());
        assertEquals(EXAMPLE_NAME, result.get(0).getName());
        assertEquals(EXAMPLE_DNI, result.get(0).getDni());
        assertEquals(EXAMPLE_EMAIL, result.get(0).getEmail());
    }

    @Test
    void isUserAdminByEmail_successful_isAdmin(){
        User newUser = new User(
                EXAMPLE_NAME,
                EXAMPLE_DNI,
                EXAMPLE_EMAIL,
                EXAMPLE_LAST_NAME
        );
        newUser.setId(EXAMPLE_ID);

        when(this.userRepository.existsByEmail(EXAMPLE_EMAIL)).thenReturn(true);
        when(this.userRepository.findByEmail(EXAMPLE_EMAIL)).thenReturn(Optional.of(newUser));
        when(this.adminRepository.existsByUserId(EXAMPLE_ID)).thenReturn(true);

        assertTrue(this.userService.isUserAdminByEmail(EXAMPLE_EMAIL));
    }

    @Test
    void isUserAdminByEmail_successful_isNotAdmin(){
        User newUser = new User(
                EXAMPLE_NAME,
                EXAMPLE_DNI,
                EXAMPLE_EMAIL,
                EXAMPLE_LAST_NAME
        );
        newUser.setId(EXAMPLE_ID);

        when(this.userRepository.existsByEmail(EXAMPLE_EMAIL)).thenReturn(true);
        when(this.userRepository.findByEmail(EXAMPLE_EMAIL)).thenReturn(Optional.of(newUser));
        when(this.adminRepository.existsByUserId(EXAMPLE_ID)).thenReturn(false);

        assertFalse(this.userService.isUserAdminByEmail(EXAMPLE_EMAIL));
    }

    @Test
    void isUserAdminByEmail_whenUserNotExists_throwsNotFoundException(){
        when(this.userRepository.existsByEmail(EXAMPLE_EMAIL)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> {
            this.userService.isUserAdminByEmail(EXAMPLE_EMAIL);
        });
    }

    @Test
    void updateUserAdminUpdateDTO_ChangeUserRolToAdmin_successful(){
        UserAdminUpdateDTO userAdminUpdateDTO = new UserAdminUpdateDTO(EXAMPLE_DNI,EXAMPLE_EMAIL,true,
                EXAMPLE_NAME,EXAMPLE_LAST_NAME,true);
        User currentUser = new User(EXAMPLE_OTHER_NAME,EXAMPLE_OTHER_DNI,EXAMPLE_OTHER_EMAIL,EXAMPLE_OTHER_LAST_NAME);
        Client client = new Client();
        client.setUser(currentUser);

        when(this.userRepository.existsById(EXAMPLE_ID)).thenReturn(true);
        when(this.userRepository.findById(EXAMPLE_ID)).thenReturn(Optional.of(currentUser));
        when(this.clientRepository.findByUserId(EXAMPLE_ID)).thenReturn(Optional.of(client));

        when(this.userRepository.existsByEmail(EXAMPLE_EMAIL)).thenReturn(false);
        when(this.userRepository.existsByDni(EXAMPLE_DNI)).thenReturn(false);
        when(this.passwordGenerator.generateStrongPassword()).thenReturn(EXAMPLE_PASSWORD);
        when(this.passwordEncoder.encode(EXAMPLE_PASSWORD)).thenReturn(EXAMPLE_ENCODED_PASSWORD);
        when(this.clientRepository.existsByUserId(EXAMPLE_ID)).thenReturn(true);
        when(this.adminRepository.existsByUserId(EXAMPLE_ID)).thenReturn(true);

        doNothing()
                .when(emailService)
                .modifiedAccountEmail(any(String.class), any(String.class), any(String.class),any(String.class));

        UserDTO responseUserDto = this.userService.update(EXAMPLE_ID,userAdminUpdateDTO);

        assertEquals(responseUserDto.getName(),EXAMPLE_NAME);
        assertEquals(responseUserDto.getEmail(),EXAMPLE_EMAIL);
        assertEquals(responseUserDto.getLastName(),EXAMPLE_LAST_NAME);
    }

    @Test
    void updateUserAdminUpdateDTO_ChangeUserRolToClient_successful(){
        UserAdminUpdateDTO userAdminUpdateDTO = new UserAdminUpdateDTO(EXAMPLE_DNI,null,true,
                EXAMPLE_NAME,EXAMPLE_LAST_NAME,false);
        User currentUser = new User(EXAMPLE_OTHER_NAME,EXAMPLE_OTHER_DNI,EXAMPLE_OTHER_EMAIL,EXAMPLE_OTHER_LAST_NAME);
        Admin admin = new Admin();
        admin.setUser(currentUser);

        when(this.userRepository.existsById(EXAMPLE_ID)).thenReturn(true);
        when(this.userRepository.findById(EXAMPLE_ID)).thenReturn(Optional.of(currentUser));
        when(this.adminRepository.findByUserId(EXAMPLE_ID)).thenReturn(Optional.of(admin));

        when(this.userRepository.existsByDni(EXAMPLE_DNI)).thenReturn(false);
        when(this.passwordGenerator.generateStrongPassword()).thenReturn(EXAMPLE_PASSWORD);
        when(this.passwordEncoder.encode(EXAMPLE_PASSWORD)).thenReturn(EXAMPLE_ENCODED_PASSWORD);
        when(this.adminRepository.existsByUserId(EXAMPLE_ID)).thenReturn(true);

        doNothing()
                .when(emailService)
                .regeneratedPasswordEmail(any(String.class), any(String.class), any(String.class));

        UserDTO responseUserDto = this.userService.update(EXAMPLE_ID,userAdminUpdateDTO);

        assertEquals(responseUserDto.getName(),EXAMPLE_NAME);
        assertEquals(responseUserDto.getEmail(),EXAMPLE_OTHER_EMAIL);
        assertEquals(responseUserDto.getLastName(),EXAMPLE_LAST_NAME);
    }
    @Test
    void updateAdminUpdateDTO_whenNotExistsUser_throwsNotFoundException(){
        UserAdminUpdateDTO userAdminUpdateDTO = new UserAdminUpdateDTO(EXAMPLE_DNI,null,true,
                EXAMPLE_NAME,EXAMPLE_LAST_NAME,false);

        when(this.userRepository.existsById(EXAMPLE_ID)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> {
            this.userService.update(EXAMPLE_ID,userAdminUpdateDTO);
        });

    }
    @Test
    void updateAdminUpdateDTO_whenExistsOtherUserWithEmail_throwsBadRequestException(){
        UserAdminUpdateDTO userAdminUpdateDTO = new UserAdminUpdateDTO(EXAMPLE_DNI,EXAMPLE_EMAIL,true,
                EXAMPLE_NAME,EXAMPLE_LAST_NAME,false);

        User currentUser = new User(EXAMPLE_OTHER_NAME,EXAMPLE_OTHER_DNI,EXAMPLE_OTHER_EMAIL,EXAMPLE_OTHER_LAST_NAME);
        currentUser.setPassword(EXAMPLE_ENCODED_PASSWORD);
        currentUser.setId(0L);

        when(this.userRepository.existsById(EXAMPLE_ID)).thenReturn(true);

        when(this.userRepository.existsByEmail(EXAMPLE_EMAIL)).thenReturn(true);
        when(this.userRepository.existsByDni(EXAMPLE_DNI)).thenReturn(false);
        when(this.userRepository.findByEmail(EXAMPLE_EMAIL)).thenReturn(Optional.of(currentUser));

        assertThrows(BadRequestException.class, () -> {
            this.userService.update(EXAMPLE_ID,userAdminUpdateDTO);
        });
    }

    @Test
    void updateAdminUpdateDTO_whenExistsOtherUserWithDni_throwsBadRequestException(){
        UserAdminUpdateDTO userAdminUpdateDTO = new UserAdminUpdateDTO(EXAMPLE_DNI,EXAMPLE_EMAIL,true,
                EXAMPLE_NAME,EXAMPLE_LAST_NAME,false);
        User currentUser = new User(EXAMPLE_OTHER_NAME,EXAMPLE_OTHER_DNI,EXAMPLE_OTHER_EMAIL,EXAMPLE_OTHER_LAST_NAME);
        currentUser.setPassword(EXAMPLE_ENCODED_PASSWORD);
        currentUser.setId(0L);

        when(this.userRepository.existsById(EXAMPLE_ID)).thenReturn(true);

        when(this.userRepository.existsByEmail(EXAMPLE_EMAIL)).thenReturn(false);
        when(this.userRepository.existsByDni(EXAMPLE_DNI)).thenReturn(true);
        when(this.userRepository.findByDni(EXAMPLE_DNI)).thenReturn(Optional.of(currentUser));

        assertThrows(BadRequestException.class, () -> {
            this.userService.update(EXAMPLE_ID,userAdminUpdateDTO);
        });
    }
    @Test
    void updateSelfUpdateDTO_ChangeUserRolToClient_successful(){
        UserSelfUpdateDTO userSelfUpdateDTO = new UserSelfUpdateDTO(EXAMPLE_DNI,EXAMPLE_EMAIL,EXAMPLE_OTHER_PASSWORD,
                EXAMPLE_PASSWORD,EXAMPLE_PASSWORD,
                EXAMPLE_NAME,EXAMPLE_LAST_NAME);
        User currentUser = new User(EXAMPLE_OTHER_NAME,EXAMPLE_OTHER_DNI,EXAMPLE_OTHER_EMAIL,EXAMPLE_OTHER_LAST_NAME);
        currentUser.setPassword(EXAMPLE_ENCODED_PASSWORD);

        when(this.userRepository.existsById(EXAMPLE_ID)).thenReturn(true);
        when(this.userRepository.findById(EXAMPLE_ID)).thenReturn(Optional.of(currentUser));

        when(this.userRepository.existsByEmail(EXAMPLE_EMAIL)).thenReturn(false);
        when(this.userRepository.existsByDni(EXAMPLE_DNI)).thenReturn(false);
        when(this.passwordEncoder.matches(EXAMPLE_OTHER_PASSWORD,EXAMPLE_ENCODED_PASSWORD)).thenReturn(true);
        when(this.passwordEncoder.encode(EXAMPLE_PASSWORD)).thenReturn(EXAMPLE_ENCODED_PASSWORD);
        when(this.adminRepository.existsByUserId(EXAMPLE_ID)).thenReturn(true);

        doNothing()
                .when(emailService)
                .oldAccountEmail(any(String.class), any(String.class), any(String.class));

        doNothing()
                .when(emailService)
                .modifiedAccountEmail(any(String.class), any(String.class), any(String.class), any(String.class));

        UserDTO responseUserDto = this.userService.update(EXAMPLE_ID,userSelfUpdateDTO);

        assertEquals(responseUserDto.getName(),EXAMPLE_NAME);
        assertEquals(responseUserDto.getEmail(),EXAMPLE_EMAIL);
        assertEquals(responseUserDto.getLastName(),EXAMPLE_LAST_NAME);
    }
    @Test
    void updateSelfUpdateDTO_whenNotExistsUser_throwsNotFoundException(){
        UserSelfUpdateDTO userSelfUpdateDTO = new UserSelfUpdateDTO(EXAMPLE_DNI,EXAMPLE_EMAIL,EXAMPLE_OTHER_PASSWORD,
                EXAMPLE_PASSWORD,EXAMPLE_PASSWORD,
                EXAMPLE_NAME,EXAMPLE_LAST_NAME);

        when(this.userRepository.existsById(EXAMPLE_ID)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> {
            this.userService.update(EXAMPLE_ID,userSelfUpdateDTO);
        });

    }
    @Test
    void updateSelfUpdateDTO_whenExistsOtherUserWithEmail_throwsBadRequestException(){
        UserSelfUpdateDTO userSelfUpdateDTO = new UserSelfUpdateDTO(EXAMPLE_DNI,EXAMPLE_EMAIL,EXAMPLE_OTHER_PASSWORD,
                EXAMPLE_PASSWORD,EXAMPLE_PASSWORD,
                EXAMPLE_NAME,EXAMPLE_LAST_NAME);
        User currentUser = new User(EXAMPLE_OTHER_NAME,EXAMPLE_OTHER_DNI,EXAMPLE_OTHER_EMAIL,EXAMPLE_OTHER_LAST_NAME);
        currentUser.setPassword(EXAMPLE_ENCODED_PASSWORD);
        currentUser.setId(0L);

        when(this.userRepository.existsById(EXAMPLE_ID)).thenReturn(true);
        when(this.userRepository.findById(EXAMPLE_ID)).thenReturn(Optional.of(currentUser));

        when(this.userRepository.existsByEmail(EXAMPLE_EMAIL)).thenReturn(true);
        when(this.userRepository.existsByDni(EXAMPLE_DNI)).thenReturn(false);
        when(this.userRepository.findByEmail(EXAMPLE_EMAIL)).thenReturn(Optional.of(currentUser));

        assertThrows(BadRequestException.class, () -> {
            this.userService.update(EXAMPLE_ID,userSelfUpdateDTO);
        });
    }

    @Test
    void updateSelfUpdateDTO_whenExistsOtherUserWithDni_throwsBadRequestException(){
        UserSelfUpdateDTO userSelfUpdateDTO = new UserSelfUpdateDTO(EXAMPLE_DNI,EXAMPLE_EMAIL,EXAMPLE_OTHER_PASSWORD,
                EXAMPLE_PASSWORD,EXAMPLE_PASSWORD,
                EXAMPLE_NAME,EXAMPLE_LAST_NAME);
        User currentUser = new User(EXAMPLE_OTHER_NAME,EXAMPLE_OTHER_DNI,EXAMPLE_OTHER_EMAIL,EXAMPLE_OTHER_LAST_NAME);
        currentUser.setPassword(EXAMPLE_ENCODED_PASSWORD);
        currentUser.setId(0L);

        when(this.userRepository.existsById(EXAMPLE_ID)).thenReturn(true);
        when(this.userRepository.findById(EXAMPLE_ID)).thenReturn(Optional.of(currentUser));

        when(this.userRepository.existsByEmail(EXAMPLE_EMAIL)).thenReturn(false);
        when(this.userRepository.existsByDni(EXAMPLE_DNI)).thenReturn(true);
        when(this.userRepository.findByDni(EXAMPLE_DNI)).thenReturn(Optional.of(currentUser));

        assertThrows(BadRequestException.class, () -> {
            this.userService.update(EXAMPLE_ID,userSelfUpdateDTO);
        });
    }

    @Test
    void updateSelfUpdateDTO_whenOldPasswordIsWrong_throwsConflictException(){
        UserSelfUpdateDTO userSelfUpdateDTO = new UserSelfUpdateDTO(EXAMPLE_DNI,EXAMPLE_EMAIL,EXAMPLE_OTHER_PASSWORD,
                EXAMPLE_PASSWORD,EXAMPLE_PASSWORD,
                EXAMPLE_NAME,EXAMPLE_LAST_NAME);
        User currentUser = new User(EXAMPLE_OTHER_NAME,EXAMPLE_OTHER_DNI,EXAMPLE_OTHER_EMAIL,EXAMPLE_OTHER_LAST_NAME);
        currentUser.setPassword(EXAMPLE_ENCODED_PASSWORD);
        currentUser.setId(0L);

        when(this.userRepository.existsById(EXAMPLE_ID)).thenReturn(true);
        when(this.userRepository.findById(EXAMPLE_ID)).thenReturn(Optional.of(currentUser));

        when(this.userRepository.existsByEmail(EXAMPLE_EMAIL)).thenReturn(false);
        when(this.userRepository.existsByDni(EXAMPLE_DNI)).thenReturn(false);
        when(this.passwordEncoder.matches(any(String.class),any(String.class))).thenReturn(false);

        assertThrows(ConflictException.class, () -> {
            this.userService.update(EXAMPLE_ID,userSelfUpdateDTO);
        });
    }

    @Test
    void updateSelfUpdateDTO_whenNewPasswordsNotMatches_throwsConflictException(){
        UserSelfUpdateDTO userSelfUpdateDTO = new UserSelfUpdateDTO(EXAMPLE_DNI,EXAMPLE_EMAIL,EXAMPLE_OTHER_PASSWORD,
                EXAMPLE_PASSWORD,"badPass",
                EXAMPLE_NAME,EXAMPLE_LAST_NAME);
        User currentUser = new User(EXAMPLE_OTHER_NAME,EXAMPLE_OTHER_DNI,EXAMPLE_OTHER_EMAIL,EXAMPLE_OTHER_LAST_NAME);
        currentUser.setPassword(EXAMPLE_ENCODED_PASSWORD);
        currentUser.setId(0L);

        when(this.userRepository.existsById(EXAMPLE_ID)).thenReturn(true);
        when(this.userRepository.findById(EXAMPLE_ID)).thenReturn(Optional.of(currentUser));

        when(this.userRepository.existsByEmail(EXAMPLE_EMAIL)).thenReturn(false);
        when(this.userRepository.existsByDni(EXAMPLE_DNI)).thenReturn(false);
        when(this.passwordEncoder.matches(any(String.class),any(String.class))).thenReturn(true);

        assertThrows(ConflictException.class, () -> {
            this.userService.update(EXAMPLE_ID,userSelfUpdateDTO);
        });
    }
}
