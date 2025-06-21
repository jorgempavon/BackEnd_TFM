package com.example.library.services;

import com.example.library.api.exceptions.models.BadRequestException;
import com.example.library.api.exceptions.models.ConflictException;
import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.config.PasswordService;
import com.example.library.entities.dto.*;
import com.example.library.entities.model.Admin;
import com.example.library.entities.model.Client;
import com.example.library.entities.model.User;
import com.example.library.entities.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    private PasswordService passwordService;
    private final EmailService emailService;
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository,
                       PasswordService passwordService,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordService = passwordService;
    }

    public UserDTO findById(Long id){
        if(!this.userRepository.existsById(id)){
            throw new NotFoundException("No existe ningún usuario con el id: "+id.toString());
        }
        User user = this.userRepository.findById(id).get();
        return new UserDTO(user.getId(),user.getName(),user.getEmail(),user.getDni(),user.getLastName(),user.getRol());
    }
    public Map<String, Object> getUserAdminStatusAndIdByEmail(String email) {
        if (!this.userRepository.existsByEmail(email)) {
            throw new NotFoundException("No existe ningún usuario con el email: " + email);
        }
        User userOpt = this.userRepository.findByEmail(email).get();
        Long userId = userOpt.getId();
        boolean isAdmin = this.adminRepository.existsByUserId(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("id", userId);
        result.put("isAdmin", isAdmin);
        return result;
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
            UserDTO newUserDto = new UserDTO(user.getId(),user.getName(),user.getEmail(),user.getDni(),user.getLastName(),user.getRol());
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

        return new UserDTO(user.getId(),user.getName(),user.getEmail(),user.getDni(),user.getLastName(),userIsAdmin);
    }
    @Transactional
    public UserDTO update(Long id, UserSelfUpdateDTO userSelfUpdateDTO){
        validateDataToUpdate(id, userSelfUpdateDTO);

        User user = this.userRepository.findById(id).get();
        updateUserData(user,userSelfUpdateDTO);

        if (userSelfUpdateDTO.getPassword() != null && !userSelfUpdateDTO.getPassword().isBlank()) {
            String encodedPassword = this.passwordService.encodePasswords(userSelfUpdateDTO.getPassword());
            user.setPassword(encodedPassword);
        }
        this.userRepository.save(user);
        return new UserDTO(user.getId(),user.getName(),user.getEmail(),user.getDni(),user.getLastName(),user.getRol());
    }

    private void updateUserData(User user, UserSelfUpdateDTO userSelfUpdateDTO) {
        String newDni = userSelfUpdateDTO.getDni();
        String newEmail = userSelfUpdateDTO.getEmail();
        String newName = userSelfUpdateDTO.getName();
        String newLastName = userSelfUpdateDTO.getLastName();

        if (newDni != null && !newDni.isEmpty()  && !newDni.equals(user.getDni())) {
            user.setDni(newDni);
        }
        if (newName != null && !newName.isEmpty() && !newName.equals(user.getName())) {
            user.setName(newName);
        }
        if (newLastName != null && !newLastName.equals(user.getLastName())) {
            user.setLastName(newLastName);
        }
        if (newEmail != null && !newEmail.isEmpty() && !newEmail.equals(user.getEmail())) {
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

        if (newDni != null  && !newDni.isEmpty() && !newDni.equals(user.getDni())) {
            user.setDni(newDni);
        }
        if (newName != null &&  !newName.isEmpty() && !newName.equals(user.getName())) {
            user.setName(newName);
        }
        if (newLastName != null && !newLastName.equals(user.getLastName())) {
            user.setLastName(newLastName);
        }
        if (newEmail != null && !newEmail.isEmpty() && !newEmail.equals(user.getEmail())) {
            this.emailService.oldAccountEmail(user.getEmail(),newEmail,user.getName() + " "+ user.getLastName());
            String infoNewPassword = "";
            if(userAdminUpdateDTO.getResetPassword()){
                infoNewPassword = this.passwordService.generateStrongPassword();
                String newEncodedPassword = this.passwordService.encodePasswords(infoNewPassword);
                user.setPassword(newEncodedPassword);
            }
            this.emailService.modifiedAccountEmail(user.getEmail(),newEmail,user.getName() + " "+
                    user.getLastName(),infoNewPassword);
            user.setEmail(newEmail);
        } else if (userAdminUpdateDTO.getResetPassword()) {
            String newPassword = this.passwordService.generateStrongPassword();
            String newEncodedPassword = this.passwordService.encodePasswords(newPassword);
            this.emailService.regeneratedPasswordEmail(user.getEmail(),user.getName() + " "+
                    user.getLastName(),newPassword);
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
        Boolean existUserWithEmail = (Boolean) responseExistsUser.get(statusEmail)
                && !Objects.equals((Long) responseExistsUser.get(idUserByEmail), id);
        Boolean existUserWithDni =  (Boolean) responseExistsUser.get(statusDni)
                && !Objects.equals((Long) responseExistsUser.get(idUserByDni), id);

        if (existUserWithEmail || existUserWithDni) {
            throw new BadRequestException((String) responseExistsUser.get(MESSAGE));
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
        Boolean existUserWithNewEmail = (Boolean) responseExistsUser.get(statusEmail)
                && !Objects.equals((Long) responseExistsUser.get(idUserByEmail), id);
        Boolean existUserWithNewDni =  (Boolean) responseExistsUser.get(statusDni)
                && !Objects.equals((Long) responseExistsUser.get(idUserByDni), id);

        if (existUserWithNewEmail || existUserWithNewDni) {
            throw new BadRequestException((String) responseExistsUser.get(MESSAGE));
        }

        String currentUserPassword = currentUser.getPassword();
        String oldPassword = userSelfUpdateDTO.getOldPassword();
        String newPassword = userSelfUpdateDTO.getPassword();
        String newRepeatPassword = userSelfUpdateDTO.getRepeatPassword();

        boolean oldPasswordProvided = oldPassword != null && !oldPassword.isBlank();
        boolean isNewPasswordProvided = ((newPassword != null && !newPassword.isBlank())
                || (newRepeatPassword != null && !newRepeatPassword.isBlank()));

        if ( (oldPasswordProvided && !this.passwordService.matchesPasswords(oldPassword,currentUserPassword))
                || (isNewPasswordProvided && !Objects.equals(newPassword, newRepeatPassword))){
            throw new ConflictException("Las contraseñas proporcionadas no son validas");
        }
    }
}
