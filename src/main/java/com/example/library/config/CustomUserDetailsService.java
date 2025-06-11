package com.example.library.config;

import com.example.library.api.exceptions.models.UnauthorizedException;
import com.example.library.entities.model.User;
import com.example.library.entities.repository.AdminRepository;
import com.example.library.entities.repository.ClientRepository;
import com.example.library.entities.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final AdminRepository adminRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository,
                                    ClientRepository clientRepository,
                                    AdminRepository adminRepository) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UnauthorizedException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UnauthorizedException("El email o contraseña proporcionados son incorrectos:" + username));

        return new CustomUserDetails(user, clientRepository, adminRepository);
    }
}
