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
    private static final Long exampleId = 2L;
    private static final String exampleName = "example";
    private static final String exampleLastName = "last name example";
    private static final String exampleEmail = "test@example.com";
    private static final String examplePass = "pass123";
    private static final String exampleOtherPass = "OtherPass123";
    private static final String exampleEncodedPass = "encodedPass";
    private static final String exampleDni = "12345678A";
    private static final String exampleOtherEmail = "other@example.com";
    private static final String exampleOtherName = "Other Name";
    private static final String exampleOtherLastName = "Other Last Name";
    private static final String exampleOtherDni = "23345452F";

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
            exampleLastName,
            true
    );
    private static final User user = new User(
            exampleName,
            exampleDni,
            exampleEmail,
            exampleLastName
    );

    @Test
    void findById_successful(){
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setDni(user.getDni());
        userDTO.setEmail(user.getEmail());
        userDTO.setName(user.getName());
        userDTO.setLastName(user.getLastName());
        userDTO.setIsAdmin(false);

        when(this.userRepository.existsById(exampleId)).thenReturn(true);
        when(this.adminRepository.existsByUserId(exampleId)).thenReturn(false);
        when(userRepository.findById(exampleId)).thenReturn(Optional.of(user));
        UserDTO result = this.userService.findById(exampleId);

        assertEquals(userDTO.getDni(), result.getDni());
        assertEquals(userDTO.getEmail(), result.getEmail());
        assertEquals(userDTO.getName(), result.getName());
        assertEquals(userDTO.getLastName(), result.getLastName());
    }

    @Test
    void findById_whenNotExistsId_throwsNotFoundException(){
        when(this.userRepository.existsById(exampleId)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> {
            userService.findById(exampleId);
        });
    }

    @Test
    void create_registerDto_successful(){
        when(this.userRepository.existsByEmail(exampleEmail)).thenReturn(false);
        when(this.userRepository.existsByDni(exampleDni)).thenReturn(false);
        when(this.passwordEncoder.encode(examplePass)).thenReturn(exampleEncodedPass);

        UserDTO response = this.userService.create(userRegisterDTO);
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
        when(this.passwordGenerator.generateStrongPassword()).thenReturn(examplePass);
        when(this.passwordEncoder.encode(examplePass)).thenReturn(exampleEncodedPass);

        UserDTO response = this.userService.create(userCreateDTO);
        assertNotNull(response);
        assertEquals(exampleName,response.getName());
        assertEquals(exampleEmail,response.getEmail());
        assertEquals(exampleLastName,response.getLastName());
        assertEquals(true,response.getIsAdmin());
    }

    @Test
    void create_registerDto_whenExistsUserEmail_throwsBadRequestException(){
        when(this.userRepository.existsByEmail(exampleEmail)).thenReturn(true);
        when(this.userRepository.existsByDni(exampleDni)).thenReturn(false);
        when(this.userRepository.findByEmail(exampleEmail)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> {
            this.userService.create(userRegisterDTO);
        });
    }
    @Test
    void create_registerDto_whenExistsUserDni_throwsBadRequestException(){
        when(this.userRepository.existsByEmail(exampleEmail)).thenReturn(false);
        when(this.userRepository.existsByDni(exampleDni)).thenReturn(true);
        when(this.userRepository.findByDni(exampleDni)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> {
            this.userService.create(userRegisterDTO);
        });
    }

    @Test
    void create_createDto_whenExistsEmail_throwsBadRequestException(){
        when(this.userRepository.existsByEmail(exampleEmail)).thenReturn(true);
        when(this.userRepository.existsByDni(exampleDni)).thenReturn(false);
        when(this.userRepository.findByEmail(exampleEmail)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> {
            this.userService.create(userCreateDTO);
        });
    }
    @Test
    void create_createDto_whenExistsDni_throwsBadRequestException(){
        when(this.userRepository.existsByEmail(exampleEmail)).thenReturn(false);
        when(this.userRepository.existsByDni(exampleDni)).thenReturn(true);
        when(this.userRepository.findByDni(exampleDni)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> {
            this.userService.create(userCreateDTO);
        });
    }

    @Test
    void create_createDto_whenIsNotValidEmail_throwsBadRequestException(){
        when(this.userRepository.existsByEmail(exampleEmail)).thenReturn(false);
        when(this.userRepository.existsByDni(exampleDni)).thenReturn(false);
        when(this.passwordGenerator.generateStrongPassword()).thenReturn(examplePass);
        when(this.passwordEncoder.encode(examplePass)).thenReturn(exampleEncodedPass);

        doThrow(new BadRequestException("El correo proporcionado no existe"))
                .when(emailService)
                .newAccountEmail(any(String.class), any(String.class), any(String.class));

        assertThrows(BadRequestException.class, () -> {
            this.userService.create(userCreateDTO);
        });
    }

    @Test
    void create_registerDto_whenIsNotValidEmail_throwsBadRequestException(){
        when(this.userRepository.existsByEmail(exampleEmail)).thenReturn(false);
        when(this.userRepository.existsByDni(exampleDni)).thenReturn(false);
        when(this.passwordEncoder.encode(examplePass)).thenReturn(exampleEncodedPass);

        doThrow(new BadRequestException("El correo proporcionado no existe"))
                .when(emailService)
                .newAccountEmail(any(String.class), any(String.class), any(String.class));

        assertThrows(BadRequestException.class, () -> {
            this.userService.create(userRegisterDTO);
        });
    }

    @Test
    void delete_successful_whenUserNotExists(){
        when(this.userRepository.existsById(exampleId)).thenReturn(false);
        when(this.clientRepository.existsByUserId(exampleId)).thenReturn(false);
        when(this.adminRepository.existsByUserId(exampleId)).thenReturn(false);

        this.userService.delete(exampleId);
    }

    @Test
    void delete_successful_whenUserIsClient(){
        Client client =  new Client();
        client.setUser(user);
        when(this.userRepository.existsById(exampleId)).thenReturn(true);
        when(this.userRepository.findById(exampleId)).thenReturn(Optional.of(user));
        when(this.clientRepository.existsByUserId(exampleId)).thenReturn(true);
        when(this.clientRepository.findByUserId(exampleId)).thenReturn(Optional.of(client));

        this.userService.delete(exampleId);
    }

    @Test
    void delete_successful_whenUserIsAdmin(){
        Admin admin =  new Admin();
        admin.setUser(user);
        when(this.userRepository.existsById(exampleId)).thenReturn(true);
        when(this.userRepository.findById(exampleId)).thenReturn(Optional.of(user));
        when(this.adminRepository.findByUserId(exampleId)).thenReturn(Optional.of(admin));
        when(this.adminRepository.existsByUserId(exampleId)).thenReturn(true);

        this.userService.delete(exampleId);
    }

    @Test
    void findByNameAndDniAndEmail_successful_parametersAreEmpty(){
        List<User> mockUsers = List.of(user);

        when(userRepository.findAll(any(Specification.class))).thenReturn(mockUsers);

        List<UserDTO> result = userService.findByNameAndDniAndEmail("", "", "");

        assertEquals(1, result.size());
        assertEquals(exampleName, result.get(0).getName());
        assertEquals(exampleDni, result.get(0).getDni());
        assertEquals(exampleEmail, result.get(0).getEmail());
    }

    @Test
    void findByNameAndDniAndEmail_successful_parametersAreNull(){
        List<User> mockUsers = List.of(user);

        when(userRepository.findAll(any(Specification.class))).thenReturn(mockUsers);

        List<UserDTO> result = userService.findByNameAndDniAndEmail(null, null, null);

        assertEquals(1, result.size());
        assertEquals(exampleName, result.get(0).getName());
        assertEquals(exampleDni, result.get(0).getDni());
        assertEquals(exampleEmail, result.get(0).getEmail());
    }

    @Test
    void findByNameAndDniAndEmail_successful(){
        List<User> mockUsers = List.of(user);

        when(userRepository.findAll(any(Specification.class))).thenReturn(mockUsers);

        List<UserDTO> result = userService.findByNameAndDniAndEmail(exampleName, exampleDni,exampleEmail);

        assertEquals(1, result.size());
        assertEquals(exampleName, result.get(0).getName());
        assertEquals(exampleDni, result.get(0).getDni());
        assertEquals(exampleEmail, result.get(0).getEmail());
    }

    @Test
    void isUserAdminAndIdByEmail_successful_isAdmin(){
        User newUser = new User(
                exampleName,
                exampleDni,
                exampleEmail,
                exampleLastName
        );
        newUser.setId(exampleId);
        when(this.userRepository.existsByEmail(exampleEmail)).thenReturn(true);
        when(this.userRepository.findByEmail(exampleEmail)).thenReturn(Optional.of(newUser));
        when(this.adminRepository.existsByUserId(exampleId)).thenReturn(true);

        assertTrue((boolean) this.userService.getUserAdminStatusAndIdByEmail(exampleEmail).get("isAdmin"));
    }

    @Test
    void isUserAdminByEmail_successful_isNotAdmin(){
        User newUser = new User(
                exampleName,
                exampleDni,
                exampleEmail,
                exampleLastName
        );
        newUser.setId(exampleId);

        when(this.userRepository.existsByEmail(exampleEmail)).thenReturn(true);
        when(this.userRepository.findByEmail(exampleEmail)).thenReturn(Optional.of(newUser));
        when(this.adminRepository.existsByUserId(exampleId)).thenReturn(false);

        assertFalse((boolean) this.userService.getUserAdminStatusAndIdByEmail(exampleEmail).get("isAdmin"));
    }

    @Test
    void isUserAdminByEmail_whenUserNotExists_throwsNotFoundException(){
        when(this.userRepository.existsByEmail(exampleEmail)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> {
            this.userService.getUserAdminStatusAndIdByEmail(exampleEmail);
        });
    }

    @Test
    void updateUserAdminUpdateDTO_ChangeUserRolToAdmin_successful(){
        UserAdminUpdateDTO userAdminUpdateDTO = new UserAdminUpdateDTO(exampleDni,exampleEmail,true,
                exampleName,exampleLastName,true);
        User currentUser = new User(exampleOtherName,exampleOtherDni,exampleOtherEmail,exampleOtherLastName);
        Client client = new Client();
        client.setUser(currentUser);

        when(this.userRepository.existsById(exampleId)).thenReturn(true);
        when(this.userRepository.findById(exampleId)).thenReturn(Optional.of(currentUser));
        when(this.clientRepository.findByUserId(exampleId)).thenReturn(Optional.of(client));

        when(this.userRepository.existsByEmail(exampleEmail)).thenReturn(false);
        when(this.userRepository.existsByDni(exampleDni)).thenReturn(false);
        when(this.passwordGenerator.generateStrongPassword()).thenReturn(examplePass);
        when(this.passwordEncoder.encode(examplePass)).thenReturn(exampleEncodedPass);
        when(this.clientRepository.existsByUserId(exampleId)).thenReturn(true);
        when(this.adminRepository.existsByUserId(exampleId)).thenReturn(true);

        doNothing()
                .when(emailService)
                .modifiedAccountEmail(any(String.class), any(String.class), any(String.class),any(String.class));

        UserDTO responseUserDto = this.userService.update(exampleId,userAdminUpdateDTO);

        assertEquals(responseUserDto.getName(),exampleName);
        assertEquals(responseUserDto.getEmail(),exampleEmail);
        assertEquals(responseUserDto.getLastName(),exampleLastName);
    }

    @Test
    void updateUserAdminUpdateDTO_ChangeUserRolToClient_successful(){
        UserAdminUpdateDTO userAdminUpdateDTO = new UserAdminUpdateDTO(exampleDni,null,true,
                exampleName,exampleLastName,false);
        User currentUser = new User(exampleOtherName,exampleOtherDni,exampleOtherEmail,exampleOtherLastName);
        Admin admin = new Admin();
        admin.setUser(currentUser);

        when(this.userRepository.existsById(exampleId)).thenReturn(true);
        when(this.userRepository.findById(exampleId)).thenReturn(Optional.of(currentUser));
        when(this.adminRepository.findByUserId(exampleId)).thenReturn(Optional.of(admin));

        when(this.userRepository.existsByDni(exampleDni)).thenReturn(false);
        when(this.passwordGenerator.generateStrongPassword()).thenReturn(examplePass);
        when(this.passwordEncoder.encode(examplePass)).thenReturn(exampleEncodedPass);
        when(this.adminRepository.existsByUserId(exampleId)).thenReturn(true);

        doNothing()
                .when(emailService)
                .regeneratedPasswordEmail(any(String.class), any(String.class), any(String.class));

        UserDTO responseUserDto = this.userService.update(exampleId,userAdminUpdateDTO);

        assertEquals(responseUserDto.getName(),exampleName);
        assertEquals(responseUserDto.getEmail(),exampleOtherEmail);
        assertEquals(responseUserDto.getLastName(),exampleLastName);
    }
    @Test
    void updateAdminUpdateDTO_whenNotExistsUser_throwsNotFoundException(){
        UserAdminUpdateDTO userAdminUpdateDTO = new UserAdminUpdateDTO(exampleDni,null,true,
                exampleName,exampleLastName,false);

        when(this.userRepository.existsById(exampleId)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> {
            this.userService.update(exampleId,userAdminUpdateDTO);
        });

    }
    @Test
    void updateAdminUpdateDTO_whenExistsOtherUserWithEmail_throwsBadRequestException(){
        UserAdminUpdateDTO userAdminUpdateDTO = new UserAdminUpdateDTO(exampleDni,exampleEmail,true,
                exampleName,exampleLastName,false);

        User currentUser = new User(exampleOtherName,exampleOtherDni,exampleOtherEmail,exampleOtherLastName);
        currentUser.setPassword(exampleEncodedPass);
        currentUser.setId(0L);

        when(this.userRepository.existsById(exampleId)).thenReturn(true);

        when(this.userRepository.existsByEmail(exampleEmail)).thenReturn(true);
        when(this.userRepository.existsByDni(exampleDni)).thenReturn(false);
        when(this.userRepository.findByEmail(exampleEmail)).thenReturn(Optional.of(currentUser));

        assertThrows(BadRequestException.class, () -> {
            this.userService.update(exampleId,userAdminUpdateDTO);
        });
    }

    @Test
    void updateAdminUpdateDTO_whenExistsOtherUserWithDni_throwsBadRequestException(){
        UserAdminUpdateDTO userAdminUpdateDTO = new UserAdminUpdateDTO(exampleDni,exampleEmail,true,
                exampleName,exampleLastName,false);
        User currentUser = new User(exampleOtherName,exampleOtherDni,exampleOtherEmail,exampleOtherLastName);
        currentUser.setPassword(exampleEncodedPass);
        currentUser.setId(0L);

        when(this.userRepository.existsById(exampleId)).thenReturn(true);

        when(this.userRepository.existsByEmail(exampleEmail)).thenReturn(false);
        when(this.userRepository.existsByDni(exampleDni)).thenReturn(true);
        when(this.userRepository.findByDni(exampleDni)).thenReturn(Optional.of(currentUser));

        assertThrows(BadRequestException.class, () -> {
            this.userService.update(exampleId,userAdminUpdateDTO);
        });
    }
    @Test
    void updateSelfUpdateDTO_ChangeUserRolToClient_successful(){
        UserSelfUpdateDTO userSelfUpdateDTO = new UserSelfUpdateDTO(exampleDni,exampleEmail,exampleOtherPass,
                examplePass,examplePass,
                exampleName,exampleLastName);
        User currentUser = new User(exampleOtherName,exampleOtherDni,exampleOtherEmail,exampleOtherLastName);
        currentUser.setPassword(exampleEncodedPass);

        when(this.userRepository.existsById(exampleId)).thenReturn(true);
        when(this.userRepository.findById(exampleId)).thenReturn(Optional.of(currentUser));

        when(this.userRepository.existsByEmail(exampleEmail)).thenReturn(false);
        when(this.userRepository.existsByDni(exampleDni)).thenReturn(false);
        when(this.passwordEncoder.matches(exampleOtherPass,exampleEncodedPass)).thenReturn(true);
        when(this.passwordEncoder.encode(examplePass)).thenReturn(exampleEncodedPass);
        when(this.adminRepository.existsByUserId(exampleId)).thenReturn(true);

        doNothing()
                .when(emailService)
                .oldAccountEmail(any(String.class), any(String.class), any(String.class));

        doNothing()
                .when(emailService)
                .modifiedAccountEmail(any(String.class), any(String.class), any(String.class), any(String.class));

        UserDTO responseUserDto = this.userService.update(exampleId,userSelfUpdateDTO);

        assertEquals(responseUserDto.getName(),exampleName);
        assertEquals(responseUserDto.getEmail(),exampleEmail);
        assertEquals(responseUserDto.getLastName(),exampleLastName);
    }
    @Test
    void updateSelfUpdateDTO_whenNotExistsUser_throwsNotFoundException(){
        UserSelfUpdateDTO userSelfUpdateDTO = new UserSelfUpdateDTO(exampleDni,exampleEmail,exampleOtherPass,
                examplePass,examplePass,
                exampleName,exampleLastName);

        when(this.userRepository.existsById(exampleId)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> {
            this.userService.update(exampleId,userSelfUpdateDTO);
        });

    }
    @Test
    void updateSelfUpdateDTO_whenExistsOtherUserWithEmail_throwsBadRequestException(){
        UserSelfUpdateDTO userSelfUpdateDTO = new UserSelfUpdateDTO(exampleDni,exampleEmail,exampleOtherPass,
                examplePass,examplePass,
                exampleName,exampleLastName);
        User currentUser = new User(exampleOtherName,exampleOtherDni,exampleOtherEmail,exampleOtherLastName);
        currentUser.setPassword(exampleEncodedPass);
        currentUser.setId(0L);

        when(this.userRepository.existsById(exampleId)).thenReturn(true);
        when(this.userRepository.findById(exampleId)).thenReturn(Optional.of(currentUser));

        when(this.userRepository.existsByEmail(exampleEmail)).thenReturn(true);
        when(this.userRepository.existsByDni(exampleDni)).thenReturn(false);
        when(this.userRepository.findByEmail(exampleEmail)).thenReturn(Optional.of(currentUser));

        assertThrows(BadRequestException.class, () -> {
            this.userService.update(exampleId,userSelfUpdateDTO);
        });
    }

    @Test
    void updateSelfUpdateDTO_whenExistsOtherUserWithDni_throwsBadRequestException(){
        UserSelfUpdateDTO userSelfUpdateDTO = new UserSelfUpdateDTO(exampleDni,exampleEmail,exampleOtherPass,
                examplePass,examplePass,
                exampleName,exampleLastName);
        User currentUser = new User(exampleOtherName,exampleOtherDni,exampleOtherEmail,exampleOtherLastName);
        currentUser.setPassword(exampleEncodedPass);
        currentUser.setId(0L);

        when(this.userRepository.existsById(exampleId)).thenReturn(true);
        when(this.userRepository.findById(exampleId)).thenReturn(Optional.of(currentUser));

        when(this.userRepository.existsByEmail(exampleEmail)).thenReturn(false);
        when(this.userRepository.existsByDni(exampleDni)).thenReturn(true);
        when(this.userRepository.findByDni(exampleDni)).thenReturn(Optional.of(currentUser));

        assertThrows(BadRequestException.class, () -> {
            this.userService.update(exampleId,userSelfUpdateDTO);
        });
    }

    @Test
    void updateSelfUpdateDTO_whenOldPasswordIsWrong_throwsConflictException(){
        UserSelfUpdateDTO userSelfUpdateDTO = new UserSelfUpdateDTO(exampleDni,exampleEmail,exampleOtherPass,
                examplePass,examplePass,
                exampleName,exampleLastName);
        User currentUser = new User(exampleOtherName,exampleOtherDni,exampleOtherEmail,exampleOtherLastName);
        currentUser.setPassword(exampleEncodedPass);
        currentUser.setId(0L);

        when(this.userRepository.existsById(exampleId)).thenReturn(true);
        when(this.userRepository.findById(exampleId)).thenReturn(Optional.of(currentUser));

        when(this.userRepository.existsByEmail(exampleEmail)).thenReturn(false);
        when(this.userRepository.existsByDni(exampleDni)).thenReturn(false);
        when(this.passwordEncoder.matches(any(String.class),any(String.class))).thenReturn(false);

        assertThrows(ConflictException.class, () -> {
            this.userService.update(exampleId,userSelfUpdateDTO);
        });
    }

    @Test
    void updateSelfUpdateDTO_whenNewPasswordsNotMatches_throwsConflictException(){
        UserSelfUpdateDTO userSelfUpdateDTO = new UserSelfUpdateDTO(exampleDni,exampleEmail,exampleOtherPass,
                examplePass,"badPass",
                exampleName,exampleLastName);
        User currentUser = new User(exampleOtherName,exampleOtherDni,exampleOtherEmail,exampleOtherLastName);
        currentUser.setPassword(exampleEncodedPass);
        currentUser.setId(0L);

        when(this.userRepository.existsById(exampleId)).thenReturn(true);
        when(this.userRepository.findById(exampleId)).thenReturn(Optional.of(currentUser));

        when(this.userRepository.existsByEmail(exampleEmail)).thenReturn(false);
        when(this.userRepository.existsByDni(exampleDni)).thenReturn(false);
        when(this.passwordEncoder.matches(any(String.class),any(String.class))).thenReturn(true);

        assertThrows(ConflictException.class, () -> {
            this.userService.update(exampleId,userSelfUpdateDTO);
        });
    }
}
