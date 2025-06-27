package com.example.library.services.user;

import com.example.library.api.exceptions.models.ConflictException;
import com.example.library.api.exceptions.models.NotFoundException;
import com.example.library.entities.dto.user.*;
import com.example.library.entities.model.user.Client;
import com.example.library.entities.model.user.User;
import com.example.library.entities.repository.user.ClientRepository;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ClientService {
    private final ClientRepository clientRepository;
    private final UserService userService;

    public ClientService( UserService userService,ClientRepository clientRepository){
        this.clientRepository = clientRepository;
        this.userService = userService;
    }

    @Transactional
    public UserDTO create(UserCreateDTO userCreateDTO){
        UserAndUserDTO responseCreateUser = this.userService.create(userCreateDTO,"client","");
        User userCreated = responseCreateUser.getUser();
        UserDTO userCreatedDTO = responseCreateUser.getUserDTO();

        Client client = new Client();
        client.setUser(userCreated);
        this.clientRepository.save(client);

        return userCreatedDTO;
    }
    @Transactional
    public void delete(Long id){
        if (this.clientRepository.existsByUserId(id)){
            Client client = this.clientRepository.findByUserId(id).get();
            this.clientRepository.delete(client);
        }
        this.userService.delete(id);
    }
    @Transactional
    public UserDTO register(UserRegisterDTO registerDTO){
        if (!Objects.equals(registerDTO.getPassword(), registerDTO.getRepeatPassword())) {
            throw new ConflictException("Las contraseñas proporcionadas no coinciden");
        }
        UserCreateDTO userCreateDTO = new UserCreateDTO(
                registerDTO.getDni(),registerDTO.getEmail(),
                registerDTO.getName(),registerDTO.getLastName()
        );
        UserAndUserDTO responseCreateUser = this.userService.create(userCreateDTO,"client",registerDTO.getPassword());
        User userCreated = responseCreateUser.getUser();
        UserDTO userCreatedDTO = responseCreateUser.getUserDTO();

        Client client = new Client();
        client.setUser(userCreated);
        this.clientRepository.save(client);

        return userCreatedDTO;
    }

    public String getUserFullNameByClient(Client client){
        if(!this.clientRepository.existsById(client.getId())){
            throw new NotFoundException("No existe el cliente proporcionado");
        }
        return this.userService.getUserFullName(client.getUser());
    }

    public Long getClientIdByUserId(Long userId){
        if(!this.clientRepository.existsByUserId(userId)){
            throw new NotFoundException("No existe el cliente proporcionado");
        }
        Client client = this.clientRepository.findByUserId(userId).get();
        return client.getId();
    }

    public Client getClientByUserId(Long userId){
        if(!this.clientRepository.existsByUserId(userId)){
            throw new NotFoundException("No existe el cliente proporcionado");
        }
        return this.clientRepository.findByUserId(userId).get();
    }

    public Boolean isClientEqualsByUserIdAndClient(Client client,Long userId){
        if(!this.userService.existsById(userId)){
            throw new NotFoundException("No existe el usuario con el id proporcionado");
        }
        if (!this.clientRepository.existsByUserId(userId)){
            return false;
        }
        if(!this.clientRepository.existsById(client.getId())){
            return false;
        }
        Client clientLogged = this.clientRepository.findByUserId(userId).get();

        return client.getId().equals(clientLogged.getId());
    }

    public String getUserEmailByClient(Client client){
        if(!this.clientRepository.existsById(client.getId())){
            throw new NotFoundException("No existe el cliente proporcionado");
        }
        return this.userService.getUserEmail(client.getUser());
    }

    public boolean isClientByUserId(Long userId){
        return this.clientRepository.existsByUserId(userId);
    }
}
