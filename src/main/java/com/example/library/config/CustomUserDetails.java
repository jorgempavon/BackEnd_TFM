package com.example.library.config;

import com.example.library.entities.model.User;
import com.example.library.entities.repository.AdminRepository;
import com.example.library.entities.repository.ClientRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {
    private final User user;
    private final List<GrantedAuthority> authorities;

    public CustomUserDetails(User user,
                             ClientRepository clientRepo,
                             AdminRepository adminRepo) {
        this.user = user;

        List<GrantedAuthority> roles = new ArrayList<>();

        if (clientRepo.existsByUserId(user.getId())) {
            roles.add(new SimpleGrantedAuthority("ROLE_CLIENT"));
        }
        if (adminRepo.existsByUserId(user.getId())) {
            roles.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        this.authorities = roles;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return this.user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
