package com.example.library.services.user;
import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.entities.dto.user.UserAndUserDTO;
import com.example.library.entities.dto.user.UserCreateDTO;
import com.example.library.entities.dto.user.UserDTO;
import com.example.library.entities.model.user.Admin;
import com.example.library.entities.model.user.User;
import com.example.library.entities.repository.user.AdminRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final UserService userService;

    public AdminService( UserService userService,AdminRepository adminRepository){
        this.adminRepository = adminRepository;
        this.userService = userService;
    }

    @Transactional
    public UserDTO create(UserCreateDTO userCreateDTO){
        UserAndUserDTO responseCreateUser = this.userService.create(userCreateDTO,"admin","");
        User userCreated = responseCreateUser.getUser();
        UserDTO userCreatedDTO = responseCreateUser.getUserDTO();

        Admin admin = new Admin();
        admin.setUser(userCreated);
        this.adminRepository.save(admin);

        return userCreatedDTO;
    }

    @Transactional
    public void delete(Long id){
        if (this.adminRepository.existsByUserId(id)){
            Admin admin = this.adminRepository.findByUserId(id).get();
            this.adminRepository.delete(admin);
        }
        this.userService.delete(id);
    }

    public String getUserFullNameByAdmin(Admin admin){
        if (!this.adminRepository.existsById(admin.getId())){
            throw new NotFoundException("No existe el usuario administrador proporcionado");
        }
        return this.userService.getUserFullName(admin.getUser());
    }

    public Admin getAdminByUserId(Long userId){
        if (!this.adminRepository.existsByUserId(userId)){
            throw new NotFoundException("No existe el usuario administrador con el id proporcionado");
        }
        return this.adminRepository.findByUserId(userId).get();
    }

    public boolean isAdminByUserId(Long userId){
        return this.adminRepository.existsByUserId(userId);
    }
}
