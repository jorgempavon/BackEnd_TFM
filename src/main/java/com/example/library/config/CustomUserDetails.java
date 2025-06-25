package com.example.library.config;

import com.example.library.entities.model.user.User;
import com.example.library.entities.repository.user.AdminRepository;
import com.example.library.entities.repository.user.ClientRepository;
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
    public Long getId(){
        return user.getId();
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
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
