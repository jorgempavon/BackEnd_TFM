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
        return new UserDTO(user.getId(),user.getName(),user.getEmail(),user.getDni(),user.getLastName(),isAdmin);
    }

    public boolean isUserAdminByEmail(String email){
        if(!this.userRepository.existsByEmail(email)){
            throw new NotFoundException("No existe ningún usuario con el email: "+email);
        }
        Long userId = this.userRepository.findByEmail(email).get().getId();
        return this.adminRepository.existsByUserId(userId);
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
            UserDTO newUserDto = new UserDTO(user.getId(),user.getName(),user.getEmail(),user.getDni(),user.getLastName(),false);
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
        updateUserData(user,userAdminUpdateDTO);
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
        return new UserDTO(user.getId(),user.getName(),user.getEmail(),user.getDni(),user.getLastName(),userIsAdmin);
    }
    @Transactional
    public UserDTO update(Long id, UserSelfUpdateDTO userSelfUpdateDTO){
        validateDataToUpdate(id, userSelfUpdateDTO);

        User user = this.userRepository.findById(id).get();
        updateUserData(user,userSelfUpdateDTO);

        if (userSelfUpdateDTO.getPassword() != null) {
            String encodedPassword = this.passwordEncoder.encode(userSelfUpdateDTO.getPassword());
            user.setPassword(encodedPassword);
        }
        this.userRepository.save(user);

        boolean isAdminUser = this.adminRepository.existsByUserId(id);
        return new UserDTO(user.getId(),user.getName(),user.getEmail(),user.getDni(),user.getLastName(),isAdminUser);
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
    private void updateUserData(User user, UserSelfUpdateDTO userSelfUpdateDTO) {
        String newDni = userSelfUpdateDTO.getDni();
        String newEmail = userSelfUpdateDTO.getEmail();
        String newName = userSelfUpdateDTO.getName();
        String newLastName = userSelfUpdateDTO.getLastName();

        if (newDni != null && !newDni.equals(user.getDni())) {
            user.setDni(newDni);
        }
        if (newName != null && !newName.equals(user.getName())) {
            user.setName(newName);
        }
        if (newLastName != null && !newLastName.equals(user.getLastName())) {
            user.setLastName(newLastName);
        }
        if (newEmail != null && !newEmail.equals(user.getEmail())) {
            this.emailService.oldAccountEmail(user.getEmail(),newEmail,user.getName() + " "+ user.getLastName());
            this.emailService.modifiedAccountEmail(user.getEmail(),newEmail,user.getName() + " "+ user.getLastName(),"");
            user.setEmail(newEmail);
        }
    }

    private void updateUserData(User user, UserAdminUpdateDTO userAdminUpdateDTO) {
        String newDni = userAdminUpdateDTO.getDni();
        String newEmail = userAdminUpdateDTO.getEmail();
        String newName = userAdminUpdateDTO.getName();
        String newLastName = userAdminUpdateDTO.getLastName();

        if (newDni != null && !newDni.equals(user.getDni())) {
            user.setDni(newDni);
        }
        if (newName != null && !newName.equals(user.getName())) {
            user.setName(newName);
        }
        if (newLastName != null && !newLastName.equals(user.getLastName())) {
            user.setLastName(newLastName);
        }
        if (newEmail != null && !newEmail.equals(user.getEmail())) {
            this.emailService.oldAccountEmail(user.getEmail(),newEmail,user.getName() + " "+ user.getLastName());
            String infoNewPassword = "";
            if(userAdminUpdateDTO.getResetPassword()){
                infoNewPassword = this.passwordGenerator.generateStrongPassword();
                String newEncodedPassword = this.passwordEncoder.encode(infoNewPassword);
                user.setPassword(newEncodedPassword);
            }
            this.emailService.modifiedAccountEmail(user.getEmail(),newEmail,user.getName() + " "+ user.getLastName(),infoNewPassword);
            user.setEmail(newEmail);
        } else if (userAdminUpdateDTO.getResetPassword()) {
            String newPassword = this.passwordGenerator.generateStrongPassword();
            String newEncodedPassword = this.passwordEncoder.encode(newPassword);
            this.emailService.regeneratedPasswordEmail(user.getEmail(),user.getName() + " "+ user.getLastName(),newPassword);
            user.setPassword(newEncodedPassword);
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
        User currentUser = this.userRepository.findById(id).get();

        Map<String, Object> responseExistsUser = this.checkUserExistence(
                userSelfUpdateDTO.getEmail(),
                userSelfUpdateDTO.getDni()
        );
        Boolean existUserWithNewEmail = (Boolean) responseExistsUser.get("statusEmail")
                && !Objects.equals((Long) responseExistsUser.get("idUserByEmail"), id);
        Boolean existUserWithNewDni =  (Boolean) responseExistsUser.get("statusDni")
                && !Objects.equals((Long) responseExistsUser.get("idUserByDni"), id);

        if (existUserWithNewEmail || existUserWithNewDni) {
            throw new BadRequestException((String) responseExistsUser.get("message"));
        }

        String currentUserPassword = currentUser.getPassword();
        String oldPassword = userSelfUpdateDTO.getOldPassword();
        String newPassword = userSelfUpdateDTO.getPassword();
        String newRepeatPassword = userSelfUpdateDTO.getRepeatPassword();

        boolean oldPasswordProvided = oldPassword != null;
        boolean isNewPasswordProvided = (newPassword != null || newRepeatPassword != null);

        if ( (oldPasswordProvided && !this.passwordEncoder.matches(oldPassword,currentUserPassword))
                || (isNewPasswordProvided && !Objects.equals(newPassword, newRepeatPassword))){
            throw new ConflictException("Las contraseñas proporcionadas no son validas");
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

        return  new UserDTO(user.getId(),user.getName(),user.getEmail(),user.getDni(),user.getLastName(),isAdmin);
    }
}
