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
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    private final EmailService emailService;
    private final PasswordGenerator passwordGenerator;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final AdminRepository adminRepository;

    public UserService(UserRepository userRepository,
                       ClientRepository clientRepository,
                       AdminRepository adminRepository,
                       PasswordEncoder passwordEncoder,
                       PasswordGenerator passwordGenerator,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordGenerator = passwordGenerator;
        this.emailService = emailService;
    }
    public UserDTO findById(Long id){
        if(!this.userRepository.existsById(id)){
            throw new NotFoundException("No existe ningún usuario con el id: "+id.toString());
        }
        User user = this.userRepository.findById(id).get();
        boolean isAdmin = false;
        if(this.adminRepository.existsByUserId(id)){
            isAdmin = true;
        }

        UserDTO newUserDTO = new UserDTO();
        newUserDTO.setId(user.getId());
        newUserDTO.setDni(user.getDni());
        newUserDTO.setEmail(user.getEmail());
        newUserDTO.setName(user.getName());
        newUserDTO.setLastName(user.getLastName());
        newUserDTO.setIsAdmin(isAdmin);

        return newUserDTO;
    }

    public UserDTO create(UserCreateDTO userCreateDTO){
        Map<String, Object> responseExistsUser = this.checkUserExistence(
                userCreateDTO.getEmail(),
                userCreateDTO.getDni()
        );

        if ((Boolean) responseExistsUser.get("status")) {
            throw new BadRequestException((String) responseExistsUser.get("message"));
        }
        String generatedPassword = this.passwordGenerator.generateStrongPassword();
        String passwordEncoded = passwordEncoder.encode(generatedPassword);
        UserSaveDTO userSaveDTO = new UserSaveDTO(
                userCreateDTO.getDni(),
                userCreateDTO.getEmail(),
                userCreateDTO.getName(),
                userCreateDTO.getLastName(),
                passwordEncoded,
                userCreateDTO.getIsAdmin()
        );
        String userFullName = userSaveDTO.getName() +" "+ userSaveDTO.getLastName();
        this.emailService.newAccountEmail(userSaveDTO.getEmail(),userFullName,generatedPassword);
        return this.save(userSaveDTO);
    }

    public UserDTO create(UserRegisterDTO userRegisterDTO){
        Map<String, Object> responseExistsUser = this.checkUserExistence(
                userRegisterDTO.getEmail(),
                userRegisterDTO.getDni()
        );

        if ((Boolean) responseExistsUser.get("status")) {
            throw new BadRequestException((String) responseExistsUser.get("message"));
        }

        String passwordEncoded = passwordEncoder.encode(userRegisterDTO.getPassword());
        UserSaveDTO userSaveDTO = new UserSaveDTO(
                userRegisterDTO.getDni(),
                userRegisterDTO.getEmail(),
                userRegisterDTO.getName(),
                userRegisterDTO.getLastName(),
                passwordEncoded,
                false
        );
        String userFullName = userRegisterDTO.getName()+" "+ userRegisterDTO.getLastName();
        this.emailService.newAccountEmail(userRegisterDTO.getEmail(),userFullName,"");
        return this.save(userSaveDTO);
    }
    public List<UserDTO> findByNameAndDniAndEmail(String name, String dni, String email) {
        Specification<User> spec = Specification.where(null);

        if (name != null && !name.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%")
            );
        }

        if (dni != null && !dni.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("dni")), "%" + dni.toLowerCase() + "%")
            );
        }

        if (email != null && !email.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%")
            );
        }

        List<User> users = this.userRepository.findAll(spec);
        List<UserDTO> responseList= new ArrayList<>();

        for (User user : users) {
            UserDTO newUserDto = new UserDTO(user.getName(),user.getEmail(),user.getDni(),user.getLastName(),false);
            newUserDto.setId(user.getId());
            if (this.adminRepository.existsByUserId(user.getId())){
                newUserDto.setIsAdmin(true);
            }
            responseList.add(newUserDto);
        }

        return responseList;
    }

    @Transactional
    public UserDTO update(Long id, UserAdminUpdateDTO userAdminUpdateDTO){
        validateDataToUpdate(id,userAdminUpdateDTO);

        User user = this.userRepository.findById(id).get();
        Map<String, String> updates = Map.of(
                "dni", userAdminUpdateDTO.getDni(),
                "email", userAdminUpdateDTO.getEmail(),
                "name", userAdminUpdateDTO.getName(),
                "lastName", userAdminUpdateDTO.getLastName()
        );
        updateUserData(user,updates);
        this.userRepository.save(user);

        boolean isUpdateToAdmin = userAdminUpdateDTO.getIsAdmin() != null && userAdminUpdateDTO.getIsAdmin();
        boolean isUpdateToClient = userAdminUpdateDTO.getIsAdmin() != null && !userAdminUpdateDTO.getIsAdmin();
        if (isUpdateToAdmin && this.clientRepository.existsByUserId(id)){
            Client clientToDelete = this.clientRepository.findByUserId(id).get();
            this.clientRepository.delete(clientToDelete);
            Admin admin = new Admin();
            admin.setUser(user);
            this.adminRepository.save(admin);
        } else if (isUpdateToClient && this.adminRepository.existsByUserId(id)) {
            Admin adminToDelete = this.adminRepository.findByUserId(id).get();
            this.adminRepository.delete(adminToDelete);
            Client client = new Client();
            client.setUser(user);
            this.clientRepository.save(client);
        }

        boolean userIsAdmin = this.adminRepository.existsByUserId(id);
        return new UserDTO(user.getName(),user.getEmail(),user.getDni(),user.getLastName(),userIsAdmin);
    }

    @Transactional
    public UserDTO update(Long id, UserSelfUpdateDTO userSelfUpdateDTO){
        validateDataToUpdate(id, userSelfUpdateDTO);
        User user = this.userRepository.findById(id).get();

        Map<String, String> updates = Map.of(
                "dni", userSelfUpdateDTO.getDni(),
                "email", userSelfUpdateDTO.getEmail(),
                "name", userSelfUpdateDTO.getName(),
                "lastName", userSelfUpdateDTO.getLastName()
        );
        updateUserData(user,updates);

        if (userSelfUpdateDTO.getPassword() != null) {
            String encodedPassword = this.passwordEncoder.encode(userSelfUpdateDTO.getPassword());
            user.setPassword(encodedPassword);
        }
        this.userRepository.save(user);

        boolean isAdminUser = this.adminRepository.existsByUserId(id);
        return new UserDTO(user.getName(),user.getEmail(),user.getDni(),user.getLastName(),isAdminUser);
    }
    @Transactional
    public void delete(Long id){
        if (this.clientRepository.existsByUserId(id)){
            Client client = this.clientRepository.findByUserId(id).get();
            this.clientRepository.delete(client);
        } else if (this.adminRepository.existsByUserId(id)) {
            Admin admin = this.adminRepository.findByUserId(id).get();
            this.adminRepository.delete(admin);
        }

        if (this.userRepository.existsById(id)){
            User user = this.userRepository.findById(id).get();
            String userFullName = user.getName() +" "+ user.getLastName();
            this.emailService.deleteAccountEmail(user.getEmail(),userFullName);
            this.userRepository.delete(user);
        }
    }
    public Map<String, Object> checkUserExistence(String email, String dni) {
        Map<String, Object> validationResult = new HashMap<>();

        String baseMessage = "proporcionado pertenece a otro usuario. Por favor, inténtelo de nuevo";

        validationResult.put("status", false);
        validationResult.put("statusEmail", false);
        validationResult.put("statusDni", false);
        if (email != null && this.userRepository.existsByEmail(email)) {
            validationResult.put("status", true);
            validationResult.put("message", "El email " + baseMessage);
            validationResult.put("statusEmail", true);
            validationResult.put("idUserByEmail",this.userRepository.findByEmail(email).get().getId());
        }

        if ((Boolean) validationResult.get("status") && dni!=null && this.userRepository.existsByDni(dni)) {
            validationResult.put("message", "El email y dni " + baseMessage);
        } else if (dni!=null && this.userRepository.existsByDni(dni)) {
            validationResult.put("status", true);
            validationResult.put("message", "El dni " + baseMessage);
            validationResult.put("statusDni", true);
            validationResult.put("idUserByDni",this.userRepository.findByDni(dni).get().getId());
        }

        return validationResult;
    }
    private void updateUserData(User user, Map<String, String> updates) {
        String newDni = updates.get("dni");
        String newEmail = updates.get("email");
        String newName = updates.get("name");
        String newLastName = updates.get("lastName");

        if (newDni != null && !newDni.equals(user.getDni())) {
            user.setDni(newDni);
        }
        if (newEmail != null && !newEmail.equals(user.getEmail())) {
            user.setEmail(newEmail);
        }
        if (newName != null && !newName.equals(user.getName())) {
            user.setName(newName);
        }
        if (newLastName != null && !newLastName.equals(user.getLastName())) {
            user.setLastName(newLastName);
        }
    }

    private void validateDataToUpdate(Long id,UserAdminUpdateDTO userAdminUpdateDTO){
        if (!this.userRepository.existsById(id)){
            throw new NotFoundException("No existe ningún usuario con el id: "+id.toString());
        }
        Map<String, Object> responseExistsUser = this.checkUserExistence(
                userAdminUpdateDTO.getEmail(),
                userAdminUpdateDTO.getDni()
        );
        Boolean existUserWithEmail = (Boolean) responseExistsUser.get("statusEmail")
                && !Objects.equals((Long) responseExistsUser.get("idUserByEmail"), id);
        Boolean existUserWithDni =  (Boolean) responseExistsUser.get("statusDni")
                && !Objects.equals((Long) responseExistsUser.get("idUserByDni"), id);

        if (existUserWithEmail || existUserWithDni) {
            throw new BadRequestException((String) responseExistsUser.get("message"));
        }
    }

    private void validateDataToUpdate(Long id, UserSelfUpdateDTO userSelfUpdateDTO){
        if (!this.userRepository.existsById(id)){
            throw new NotFoundException("No existe ningún usuario con el id: "+id.toString());
        }

        Map<String, Object> responseExistsUser = this.checkUserExistence(
                userSelfUpdateDTO.getEmail(),
                userSelfUpdateDTO.getDni()
        );
        Boolean existUserWithEmail = (Boolean) responseExistsUser.get("statusEmail")
                && !Objects.equals((Long) responseExistsUser.get("idUserByEmail"), id);
        Boolean existUserWithDni =  (Boolean) responseExistsUser.get("statusDni")
                && !Objects.equals((Long) responseExistsUser.get("idUserByDni"), id);

        if (existUserWithEmail || existUserWithDni) {
            throw new BadRequestException((String) responseExistsUser.get("message"));
        }
        String oldPasswordProvided = userSelfUpdateDTO.getOldPassword();
        String currentUserPassword = this.userRepository.findById(id).get().getPassword();
        boolean isValidOldPassword = oldPasswordProvided!=null && this.passwordEncoder.matches(oldPasswordProvided,currentUserPassword);
        boolean isPasswordProvided = (userSelfUpdateDTO.getPassword() != null || userSelfUpdateDTO.getRepeatPassword() != null);

        if ( isValidOldPassword && isPasswordProvided &&
                !Objects.equals(userSelfUpdateDTO.getPassword(), userSelfUpdateDTO.getRepeatPassword())){
            throw new ConflictException("Las contraseñas proporcionadas no coinciden");
        }
    }
    @Transactional
    private UserDTO save(UserSaveDTO userSaveDTO){
        boolean isAdmin = userSaveDTO.getIsAdmin();
        User user = new User();
        user.setDni(userSaveDTO.getDni());
        user.setEmail(userSaveDTO.getEmail());
        user.setName(userSaveDTO.getName());
        user.setLastName(userSaveDTO.getLastName());
        user.setPassword(userSaveDTO.getPasswordEncoded());
        this.userRepository.save(user);

        if (isAdmin){
            Admin admin = new Admin();
            admin.setUser(user);
            this.adminRepository.save(admin);
        }
        else {
            Client client = new Client();
            client.setUser(user);
            this.clientRepository.save(client);
        }

        UserDTO newUserDTO = new UserDTO();
        newUserDTO.setId(user.getId());
        newUserDTO.setDni(user.getDni());
        newUserDTO.setEmail(user.getEmail());
        newUserDTO.setName(user.getName());
        newUserDTO.setLastName(user.getLastName());
        newUserDTO.setIsAdmin(isAdmin);
        return newUserDTO;
    }
}
