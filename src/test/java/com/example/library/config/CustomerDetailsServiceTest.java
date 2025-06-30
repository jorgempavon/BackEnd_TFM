package com.example.library.config;

import com.example.library.api.exceptions.models.UnauthorizedException;
import com.example.library.entities.model.user.User;
import com.example.library.entities.repository.user.AdminRepository;
import com.example.library.entities.repository.user.ClientRepository;
import com.example.library.entities.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
public class CustomerDetailsServiceTest {
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AdminRepository adminRepository;
    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private final String EXAMPLE_EMAIL= "test@example.com";
    @Test
    void loadUserByUsernameSuccessful(){
        String EXAMPLE_NAME = "Example name";
        User user = new User(EXAMPLE_NAME,"01234567L",EXAMPLE_EMAIL,"Example last name");
        when(this.userRepository.findByEmail(EXAMPLE_EMAIL))
                .thenReturn(Optional.of(user));
        CustomUserDetails response = this.customUserDetailsService.loadUserByUsername(EXAMPLE_EMAIL);
        assertNotNull(response);
        assertSame(response.getUsername(), EXAMPLE_EMAIL);

    }

    @Test
    void loadUserByUsernameWhenNotExistsThrowsUnauthorizedException(){
        when(this.userRepository.findByEmail(EXAMPLE_EMAIL))
                .thenThrow(UnauthorizedException.class);

        assertThrows(UnauthorizedException.class, () ->
            customUserDetailsService.loadUserByUsername(EXAMPLE_EMAIL));

    }
}
