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
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    private final JavaMailSender mailSender;
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
                       JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordGenerator = passwordGenerator;
        this.mailSender = mailSender;
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
        String passwordText = ",para acceder utilice la siguiente contraseña: "+ generatedPassword
                + "\nPorfavor, le recomendamos cambiar la contraseña lo antes posible";
        this.sendNewAccountEmail(userCreateDTO.getEmail(),passwordText);
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

        this.sendNewAccountEmail(userRegisterDTO.getEmail(),"");
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
    public UserDTO update(Long id, UserUpdateDTO userUpdateDTO){
        validateDataToUpdate(id,userUpdateDTO);

        User user = this.userRepository.findById(id).get();
        updateUserFromUserUpdateDto(user,userUpdateDTO);
        this.userRepository.save(user);

        boolean isUpdateToAdmin = userUpdateDTO.getIsAdmin() != null && userUpdateDTO.getIsAdmin();
        boolean isUpdateToClient = userUpdateDTO.getIsAdmin() != null && !userUpdateDTO.getIsAdmin();
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
    private void updateUserFromUserUpdateDto(User user, UserUpdateDTO userUpdateDTO) {
        if (userUpdateDTO.getDni() != null && !userUpdateDTO.getDni().equals(user.getDni())) {
            user.setDni(userUpdateDTO.getDni());
        }
        if (userUpdateDTO.getEmail() != null && !userUpdateDTO.getEmail().equals(user.getEmail())) {
            user.setEmail(userUpdateDTO.getEmail());
        }
        if (userUpdateDTO.getName() != null && !userUpdateDTO.getName().equals(user.getName())) {
            user.setName(userUpdateDTO.getName());
        }
        if (userUpdateDTO.getLastName() != null && !userUpdateDTO.getLastName().equals(user.getLastName())) {
            user.setLastName(userUpdateDTO.getLastName());
        }
        if (userUpdateDTO.getPassword() != null) {
            String encodedPassword = this.passwordEncoder.encode(userUpdateDTO.getPassword());
            user.setPassword(encodedPassword);
        }
    }
    private void validateDataToUpdate(Long id,UserUpdateDTO userUpdateDTO){
        if (!this.userRepository.existsById(id)){
            throw new NotFoundException("No existe ningún usuario con el id: "+id.toString());
        }
        Map<String, Object> responseExistsUser = this.checkUserExistence(
                userUpdateDTO.getEmail(),
                userUpdateDTO.getDni()
        );
        Boolean existUserWithEmail = (Boolean) responseExistsUser.get("statusEmail")
                && !Objects.equals((Long) responseExistsUser.get("idUserByEmail"), id);
        Boolean existUserWithDni =  (Boolean) responseExistsUser.get("statusDni")
                && !Objects.equals((Long) responseExistsUser.get("idUserByDni"), id);

        if (existUserWithEmail || existUserWithDni) {
            throw new BadRequestException((String) responseExistsUser.get("message"));
        }
        boolean isPasswordProvided = (userUpdateDTO.getPassword() != null || userUpdateDTO.getRepeatPassword() != null);
        if ( isPasswordProvided &&  !Objects.equals(userUpdateDTO.getPassword(), userUpdateDTO.getRepeatPassword())){
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
    private void sendNewAccountEmail(String email, String passwordText) {
        try{
            String subject = "Nueva Cuenta en Bibliokie";
            String body = "Ha sido dado de alta en Bibliokie" +
                    passwordText;
            String endBody="\nEste correro es meramente informativo.\n\nMuchas gracias,\nUn saludo.";

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("bibliokiejackie@gmail.com");
            message.setTo(email);
            message.setSubject(subject);
            message.setText(body + endBody);
            mailSender.send(message);
        }
        catch (Exception e){
            throw new BadRequestException("El correo proporcionado no existe. Porfavor, introduzca un nuevo.");
        }
    }
}
