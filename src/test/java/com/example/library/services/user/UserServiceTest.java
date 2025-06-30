package com.example.library.services.user;

import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.api.exceptions.models.UnauthorizedException;
import com.example.library.config.CustomUserDetails;
import com.example.library.config.CustomUserDetailsService;
import com.example.library.config.JwtService;
import com.example.library.config.PasswordService;
import com.example.library.entities.dto.user.LoginDTO;
import com.example.library.entities.dto.user.SessionDTO;
import com.example.library.entities.dto.user.UserDTO;
import com.example.library.entities.dto.user.*;
import com.example.library.entities.model.user.User;
import com.example.library.entities.repository.user.UserRepository;
import com.example.library.services.EmailService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private JwtService jwtService;
    @Mock
    private CustomUserDetailsService userDetailsService;
    @Mock
    private PasswordService passwordService;
    @Mock
    private EmailService emailService;

    @Mock
    private UserValidatorService userValidatorService;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;
    private static final Long EXAMPLE_ID = 2L;
    private static final String EXAMPLE_NAME = "example";
    private static final String EXAMPLE_LAST_NAME = "last name example";
    private static final String EXAMPLE_EMAIL = "test@example.com";
    private static final String EXAMPLE_DNI = "12345678A";
    private static final String EXAMPLE_PASS = "pass123";
    private static final LoginDTO LOGIN_DTO = new LoginDTO(
            EXAMPLE_EMAIL,EXAMPLE_PASS
    );
    private static final String EXAMPLE_ENCODED_PASS = "encodedPassword";

    private static final User USER = new User(
            EXAMPLE_ID,
            EXAMPLE_NAME,
            EXAMPLE_DNI,
            EXAMPLE_EMAIL,
            EXAMPLE_LAST_NAME
    );
    private static final String ROL = "client";

    private static final UserCreateDTO USER_CREATE_DTO = new UserCreateDTO(
            EXAMPLE_DNI,
            EXAMPLE_EMAIL,
            EXAMPLE_NAME,
            EXAMPLE_LAST_NAME
    );


    private static final String EXAMPLE_TKN = "sdjinew0vw-rewrwegrgrge0cmtgtrgrtgtgnbynhyh09";
    private static final String OLD_PASS = "old pass";
    private static final UserSelfUpdateDTO USER_SELF_UPDATE_DTO = new UserSelfUpdateDTO(
            EXAMPLE_DNI,
            EXAMPLE_EMAIL,
            OLD_PASS,
            EXAMPLE_PASS,
            EXAMPLE_PASS,
            EXAMPLE_NAME,
            EXAMPLE_LAST_NAME
    );

    @Test
    void findById_successful(){
        UserDTO userDTO = new UserDTO();
        userDTO.setId(USER.getId());
        userDTO.setDni(USER.getDni());
        userDTO.setEmail(USER.getEmail());
        userDTO.setName(USER.getName());
        userDTO.setLastName(USER.getLastName());
        userDTO.setRol(ROL);

        when(this.userRepository.existsById(EXAMPLE_ID)).thenReturn(true);
        when(userRepository.findById(EXAMPLE_ID)).thenReturn(Optional.of(USER));
        UserDTO result = this.userService.findById(EXAMPLE_ID);

        assertEquals(userDTO.getDni(), result.getDni());
        assertEquals(userDTO.getEmail(), result.getEmail());
        assertEquals(userDTO.getName(), result.getName());
        assertEquals(userDTO.getLastName(), result.getLastName());
    }

    @Test
    void findByIdWhenNotExistsIdThrowsNotFoundException(){
        when(this.userRepository.existsById(EXAMPLE_ID)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> {
            userService.findById(EXAMPLE_ID);
        });
    }

    @Test
    void findByNameAndDniAndEmailSuccessful(){
        List<User> mockUsers = List.of(USER);

        when(userRepository.findAll(any(Specification.class))).thenReturn(mockUsers);

        List<UserDTO> result = userService.findByNameAndDniAndEmail(EXAMPLE_NAME, EXAMPLE_DNI,EXAMPLE_EMAIL);

        assertEquals(1, result.size());
        assertEquals(EXAMPLE_NAME, result.get(0).getName());
        assertEquals(EXAMPLE_DNI, result.get(0).getDni());
        assertEquals(EXAMPLE_EMAIL, result.get(0).getEmail());
    }

    @Test
    void loginSuccessful(){
        CustomUserDetails mockUserDetails = mock(CustomUserDetails.class);
        when(userDetailsService.loadUserByUsername(EXAMPLE_EMAIL)).thenReturn(mockUserDetails);
        when(mockUserDetails.getPassword()).thenReturn(EXAMPLE_ENCODED_PASS);
        when(mockUserDetails.getUsername()).thenReturn(EXAMPLE_EMAIL);
        when(passwordService.matchesPasswords(EXAMPLE_PASS, EXAMPLE_ENCODED_PASS)).thenReturn(true);
        when(userRepository.findByEmail(EXAMPLE_EMAIL)).thenReturn(Optional.of(USER));

        String mockJwt = "mockedJwtToken";
        when(jwtService.generateToken(mockUserDetails)).thenReturn(mockJwt);

        SessionDTO sessionDTO = this.userService.login(LOGIN_DTO);

        assertEquals(mockJwt, sessionDTO.getJwt());
    }

    @Test
    void loginWhenNotMatchPasswordThrowsUnauthorizedException(){
        CustomUserDetails mockUserDetails = mock(CustomUserDetails.class);
        when(userDetailsService.loadUserByUsername(EXAMPLE_EMAIL)).thenReturn(mockUserDetails);
        when(mockUserDetails.getPassword()).thenReturn(EXAMPLE_ENCODED_PASS);
        when(passwordService.matchesPasswords(EXAMPLE_PASS, EXAMPLE_ENCODED_PASS)).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> {
            userService.login(LOGIN_DTO);
        });
    }
    @Test
    void logOutSuccessful(){
        CustomUserDetails mockUserDetails = mock(CustomUserDetails.class);

        when(this.jwtService.extractUsername(EXAMPLE_TKN)).thenReturn(EXAMPLE_EMAIL);
        when(this.userDetailsService.loadUserByUsername(EXAMPLE_EMAIL)).thenReturn(mockUserDetails);
        when(this.jwtService.isTokenValid(EXAMPLE_TKN, mockUserDetails)).thenReturn(true);

        this.userService.logOut(EXAMPLE_TKN);
    }
    @Test
    void logOutWhenUserNotExistsThrowsUnathorizedException(){
        when(this.jwtService.extractUsername(EXAMPLE_TKN)).thenThrow(UnauthorizedException.class);

        assertThrows(UnauthorizedException.class, () -> {
            userService.logOut(EXAMPLE_TKN);
        });
    }
    @Test
    void createUserWithNoPasswordProvidedSuccessful(){
        UserExistenceDTO userExistenceDTO = new UserExistenceDTO();
        when(this.userValidatorService.checkUserExistence(EXAMPLE_EMAIL,EXAMPLE_DNI)).thenReturn(userExistenceDTO);
        when(this.passwordService.generateStrongPassword()).thenReturn(EXAMPLE_PASS);
        when(this.passwordService.encodePasswords(EXAMPLE_PASS)).thenReturn(EXAMPLE_ENCODED_PASS);

        UserAndUserDTO responseDTO = this.userService.create(USER_CREATE_DTO,ROL,"");
        User responseUser = responseDTO.getUser();
        UserDTO responseUserDTO = responseDTO.getUserDTO();

        assertEquals(responseUser.getName(),EXAMPLE_NAME);
        assertEquals(responseUser.getLastName(),EXAMPLE_LAST_NAME);
        assertEquals(responseUser.getEmail(),EXAMPLE_EMAIL);
        assertEquals(responseUser.getDni(),EXAMPLE_DNI);

        assertEquals(responseUserDTO.getName(),EXAMPLE_NAME);
        assertEquals(responseUserDTO.getLastName(),EXAMPLE_LAST_NAME);
        assertEquals(responseUserDTO.getEmail(),EXAMPLE_EMAIL);
        assertEquals(responseUserDTO.getDni(),EXAMPLE_DNI);
        assertEquals(responseUserDTO.getRol(),ROL);
    }

    @Test
    void createUserWithPasswordProvidedSuccessful(){
        UserExistenceDTO userExistenceDTO = new UserExistenceDTO();
        when(this.userValidatorService.checkUserExistence(EXAMPLE_EMAIL,EXAMPLE_DNI)).thenReturn(userExistenceDTO);
        when(this.passwordService.encodePasswords(EXAMPLE_PASS)).thenReturn(EXAMPLE_ENCODED_PASS);

        UserAndUserDTO responseDTO = this.userService.create(USER_CREATE_DTO,ROL,EXAMPLE_PASS);
        User responseUser = responseDTO.getUser();
        UserDTO responseUserDTO = responseDTO.getUserDTO();

        assertEquals(responseUser.getName(),EXAMPLE_NAME);
        assertEquals(responseUser.getLastName(),EXAMPLE_LAST_NAME);
        assertEquals(responseUser.getEmail(),EXAMPLE_EMAIL);
        assertEquals(responseUser.getDni(),EXAMPLE_DNI);

        assertEquals(responseUserDTO.getName(),EXAMPLE_NAME);
        assertEquals(responseUserDTO.getLastName(),EXAMPLE_LAST_NAME);
        assertEquals(responseUserDTO.getEmail(),EXAMPLE_EMAIL);
        assertEquals(responseUserDTO.getDni(),EXAMPLE_DNI);
        assertEquals(responseUserDTO.getRol(),ROL);
    }
    @Test
    void getUserFullNameSuccessful(){
        when(this.userRepository.existsById(EXAMPLE_ID)).thenReturn(true);
        String response = this.userService.getUserFullName(USER);
        assertEquals(response,EXAMPLE_NAME+" "+EXAMPLE_LAST_NAME);
    }

    @Test
    void getUserFullNameNotExistsUserThrowNotFoundException(){
        when(this.userRepository.existsById(EXAMPLE_ID)).thenReturn(true);
        String response = this.userService.getUserFullName(USER);
        assertEquals(response,EXAMPLE_NAME+" "+EXAMPLE_LAST_NAME);
    }

    @Test
    void deleteSuccessfulExistsUser(){
        when(this.userRepository.existsById(EXAMPLE_ID)).thenReturn(true);
        when(this.userRepository.findById(EXAMPLE_ID)).thenReturn(Optional.of(USER));
        this.userService.delete(EXAMPLE_ID);
    }

    @Test
    void deleteSuccessfulNotExistsUser(){
        when(this.userRepository.existsById(EXAMPLE_ID)).thenReturn(false);
        this.userService.delete(EXAMPLE_ID);
    }

    @Test
    void getUserEmailSuccessful(){
        when(this.userRepository.existsById(EXAMPLE_ID)).thenReturn(true);
        String response = this.userService.getUserEmail(USER);
        assertEquals(response,EXAMPLE_EMAIL);
    }
    @Test
    void getUserEmailThrowsNotFoundException(){
        when(this.userRepository.existsById(EXAMPLE_ID)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> {
            userService.getUserEmail(USER);
        });
    }
    @Test
    void updateSelfDTOSuccessful(){
        when(this.userRepository.findById(EXAMPLE_ID)).thenReturn(Optional.of(USER));
        when(this.passwordService.encodePasswords(EXAMPLE_PASS)).thenReturn(EXAMPLE_ENCODED_PASS);
        UserDTO response = this.userService.update(EXAMPLE_ID,USER_SELF_UPDATE_DTO);

        assertEquals(response.getName(),EXAMPLE_NAME);
        assertEquals(response.getLastName(),EXAMPLE_LAST_NAME);
        assertEquals(response.getEmail(),EXAMPLE_EMAIL);
        assertEquals(response.getDni(),EXAMPLE_DNI);
    }
    @Test
    void updateAdminUpdateDTOSuccessful(){
        UserAdminUpdateDTO userAdminUpdateDTO = new UserAdminUpdateDTO(
            EXAMPLE_DNI,EXAMPLE_EMAIL,false,EXAMPLE_NAME,EXAMPLE_LAST_NAME
        );
        when(this.userRepository.findById(EXAMPLE_ID)).thenReturn(Optional.of(USER));
        UserDTO response = this.userService.update(EXAMPLE_ID,userAdminUpdateDTO);

        assertEquals(response.getName(),EXAMPLE_NAME);
        assertEquals(response.getLastName(),EXAMPLE_LAST_NAME);
        assertEquals(response.getEmail(),EXAMPLE_EMAIL);
        assertEquals(response.getDni(),EXAMPLE_DNI);
    }
    @Test
    void existsByIdReturnsTrue(){
        when(this.userRepository.existsById(EXAMPLE_ID)).thenReturn(true);
        assertTrue(this.userService.existsById(EXAMPLE_ID));
    }
    @Test
    void existsByIdReturnsFalse(){
        when(this.userRepository.existsById(EXAMPLE_ID)).thenReturn(false);
        assertFalse(this.userService.existsById(EXAMPLE_ID));
    }
}
