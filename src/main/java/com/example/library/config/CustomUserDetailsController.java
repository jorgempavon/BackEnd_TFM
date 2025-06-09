package com.example.library.config;

import com.example.library.entities.model.User;
import com.example.library.entities.repository.AdminRepository;
import com.example.library.entities.repository.ClientRepository;
import com.example.library.entities.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsController implements UserDetailsService {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final AdminRepository adminRepository;

    @Autowired
    public CustomUserDetailsController(UserRepository userRepository,
                                    ClientRepository clientRepository,
                                    AdminRepository adminRepository) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + username));

        return new CustomUserDetails(user, clientRepository, adminRepository);
    }
}
