package com.example.library.services.user;

import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.entities.dto.user.*;
import com.example.library.entities.model.user.Admin;
import com.example.library.entities.model.user.User;
import com.example.library.entities.repository.user.AdminRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {
    @Mock
    private AdminRepository adminRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private AdminService adminService;
    private static final Long EXAMPLE_ID = 2L;
    private static final Long USER_ID = 2L;
    private static final String EXAMPLE_NAME = "example";
    private static final String EXAMPLE_LAST_NAME = "last name example";
    private static final String EXAMPLE_EMAIL = "test@example.com";
    private static final String EXAMPLE_DNI = "12345678A";
    private static final String FULL_NAME = "User full name";

    private static final User USER = new User(
            EXAMPLE_NAME,
            EXAMPLE_DNI,
            EXAMPLE_EMAIL,
            EXAMPLE_LAST_NAME
    );
    private static final String ROL = "client";
    private static final UserDTO USER_DTO = new UserDTO(
            EXAMPLE_ID,
            EXAMPLE_NAME,
            EXAMPLE_EMAIL,
            EXAMPLE_DNI,
            EXAMPLE_LAST_NAME,
            ROL
    );
    private static final UserAndUserDTO USER_AND_USER_DTO = new UserAndUserDTO(
            USER,USER_DTO
    );
    private static final UserCreateDTO USER_CREATE_DTO = new UserCreateDTO(
            EXAMPLE_DNI,
            EXAMPLE_EMAIL,
            EXAMPLE_NAME,
            EXAMPLE_LAST_NAME
    );
    private static final Long ADMIN_ID = 7L;
    private static final Admin ADMIN = new Admin(ADMIN_ID,USER);

    @Test
    void createAdmin_createDto_successful(){
        when(this.userService.create(USER_CREATE_DTO,ROL,"")).thenReturn(USER_AND_USER_DTO);

        UserDTO response = this.adminService.create(USER_CREATE_DTO);
        assertNotNull(response);
        assertEquals(EXAMPLE_NAME,response.getName());
        assertEquals(EXAMPLE_EMAIL,response.getEmail());
        assertEquals(EXAMPLE_LAST_NAME,response.getLastName());
        assertEquals(ROL,response.getRol());
    }

    @Test
    void deleteAdmin_successful_whenUserNotExists(){
        when(this.adminRepository.existsByUserId(EXAMPLE_ID)).thenReturn(false);
        doNothing().when(this.userService).delete(EXAMPLE_ID);
        this.adminService.delete(EXAMPLE_ID);
    }

    @Test
    void deleteClient_successful_whenUserIsClient(){
        when(this.adminRepository.existsByUserId(EXAMPLE_ID)).thenReturn(true);
        when(this.adminRepository.findByUserId(EXAMPLE_ID)).thenReturn(Optional.of(ADMIN));
        doNothing().when(this.userService).delete(EXAMPLE_ID);

        this.adminService.delete(EXAMPLE_ID);
    }
    @Test
    void getUserFullNameByAdmin_successful(){
        when(this.adminRepository.existsById(ADMIN_ID)).thenReturn(true);
        when(this.userService.getUserFullName(USER)).thenReturn(FULL_NAME);

        String responseFullName =  this.adminService.getUserFullNameByAdmin(ADMIN);

        assertEquals(responseFullName,FULL_NAME);
    }
    @Test
    void getUserFullNameByAdmin_NotExistsAdmin_throwNotFoundException(){
        when(this.adminRepository.existsById(ADMIN_ID)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> {
            adminService.getUserFullNameByAdmin(ADMIN);
        });
    }
    @Test
    void getAdminByUserId_successful(){
        when(this.adminRepository.existsByUserId(USER_ID)).thenReturn(true);
        when(this.adminRepository.findByUserId(USER_ID)).thenReturn(Optional.of(ADMIN));

        Admin responseUser =  this.adminService.getAdminByUserId(USER_ID);

        assertEquals(responseUser,ADMIN);
    }
    @Test
    void getAdminByUserId_NotExistsAdmin_throwNotFoundException(){
        when(this.adminRepository.existsByUserId(USER_ID)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> {
            adminService.getAdminByUserId(USER_ID);
        });
    }
    @Test
    void isAdminByUserId_true(){
        when(this.adminRepository.existsByUserId(USER_ID)).thenReturn(true);
        assertTrue(this.adminService.isAdminByUserId(USER_ID));
    }
    @Test
    void isAdminByUserId_false(){
        when(this.adminRepository.existsByUserId(USER_ID)).thenReturn(false);
        assertFalse(this.adminService.isAdminByUserId(USER_ID));
    }
}
