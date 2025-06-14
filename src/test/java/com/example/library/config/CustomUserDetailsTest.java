package com.example.library.config;

import com.example.library.config.CustomUserDetails;
import com.example.library.entities.model.User;
import com.example.library.entities.repository.AdminRepository;
import com.example.library.entities.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CustomUserDetailsTest {

    private ClientRepository clientRepository;
    private AdminRepository adminRepository;
    private User user;

    @BeforeEach
    void setUp() {
        clientRepository = mock(ClientRepository.class);
        adminRepository = mock(AdminRepository.class);

        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("securepassword");
    }

    @Test
    void shouldAssignClientRoleOnly() {
        when(clientRepository.existsByUserId(1L)).thenReturn(true);
        when(adminRepository.existsByUserId(1L)).thenReturn(false);

        CustomUserDetails userDetails = new CustomUserDetails(user, clientRepository, adminRepository);

        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CLIENT")));
        assertEquals(1, userDetails.getAuthorities().size());
    }

    @Test
    void shouldAssignAdminRoleOnly() {
        when(clientRepository.existsByUserId(1L)).thenReturn(false);
        when(adminRepository.existsByUserId(1L)).thenReturn(true);

        CustomUserDetails userDetails = new CustomUserDetails(user, clientRepository, adminRepository);

        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
        assertEquals(1, userDetails.getAuthorities().size());
    }

    @Test
    void shouldReturnCorrectUserDetails() {
        CustomUserDetails userDetails = new CustomUserDetails(user, clientRepository, adminRepository);

        assertEquals(user.getEmail(), userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());
        assertEquals(user.getId(), userDetails.getId());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());
    }
}

