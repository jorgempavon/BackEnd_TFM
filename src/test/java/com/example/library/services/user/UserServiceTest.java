package com.example.library.services.user;

import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.api.exceptions.models.UnauthorizedException;
import com.example.library.config.CustomUserDetails;
import com.example.library.config.CustomUserDetailsService;
import com.example.library.config.JwtService;
import com.example.library.config.PasswordService;
import com.example.library.entities.dto.LoginDTO;
import com.example.library.entities.dto.SessionDTO;
import com.example.library.entities.dto.UserDTO;
import com.example.library.entities.model.User;
import com.example.library.entities.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;
    private static final Long exampleId = 2L;
    private static final String exampleName = "example";
    private static final String exampleLastName = "last name example";
    private static final String exampleEmail = "test@example.com";
    private static final String exampleDni = "12345678A";
    private static final String examplePass = "pass123";
    private static final LoginDTO loginDTO = new LoginDTO(
            exampleEmail,examplePass
    );
    private static final String exampleEncodedPass = "encodedPassword";

    private static final User user = new User(
            exampleName,
            exampleDni,
            exampleEmail,
            exampleLastName
    );
    private static final String exampleTkn = "sdjinew0vw-rewrwegrgrge0cmtgtrgrtgtgnbynhyh09";
    private static final String rol = "client";
    @Test
    void findById_successful(){
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setDni(user.getDni());
        userDTO.setEmail(user.getEmail());
        userDTO.setName(user.getName());
        userDTO.setLastName(user.getLastName());
        userDTO.setRol(rol);

        when(this.userRepository.existsById(exampleId)).thenReturn(true);
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
    void login_successful(){
        CustomUserDetails mockUserDetails = mock(CustomUserDetails.class);
        when(userDetailsService.loadUserByUsername(exampleEmail)).thenReturn(mockUserDetails);
        when(mockUserDetails.getPassword()).thenReturn(exampleEncodedPass);
        when(mockUserDetails.getUsername()).thenReturn(exampleEmail);
        when(passwordService.matchesPasswords(examplePass, exampleEncodedPass)).thenReturn(true);
        when(userRepository.findByEmail(exampleEmail)).thenReturn(Optional.of(user));

        String mockJwt = "mockedJwtToken";
        when(jwtService.generateToken(mockUserDetails)).thenReturn(mockJwt);

        SessionDTO sessionDTO = this.userService.login(loginDTO);

        assertEquals(mockJwt, sessionDTO.getJwt());
    }

    @Test
    void login_whenNotExistsEmail_throwsUnauthorizedException(){
        when(userDetailsService.loadUserByUsername(exampleEmail)).thenThrow(
                new UnauthorizedException("El email o contraseña proporcionados son incorrectos")
        );

        assertThrows(UnauthorizedException.class, () -> {
            userService.login(loginDTO);
        });
    }

    @Test
    void login_whenNotMatchPassword_throwsUnauthorizedException(){
        CustomUserDetails mockUserDetails = mock(CustomUserDetails.class);
        when(userDetailsService.loadUserByUsername(exampleEmail)).thenReturn(mockUserDetails);
        when(mockUserDetails.getPassword()).thenReturn(exampleEncodedPass);
        when(passwordService.matchesPasswords(examplePass, exampleEncodedPass)).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> {
            userService.login(loginDTO);
        });
    }
    @Test
    void logOut_Successful(){
        CustomUserDetails mockUserDetails = mock(CustomUserDetails.class);

        when(this.jwtService.extractUsername(exampleTkn)).thenReturn(exampleEmail);
        when(this.userDetailsService.loadUserByUsername(exampleEmail)).thenReturn(mockUserDetails);
        when(this.jwtService.isTokenValid(exampleTkn, mockUserDetails)).thenReturn(true);

        this.userService.logOut(exampleTkn);
    }
    @Test
    void logOut_whenUserNotExists_throwsUnathorizedException(){
        when(this.jwtService.extractUsername(exampleTkn)).thenThrow(UnauthorizedException.class);

        assertThrows(UnauthorizedException.class, () -> {
            userService.logOut(exampleTkn);
        });
    }
}
