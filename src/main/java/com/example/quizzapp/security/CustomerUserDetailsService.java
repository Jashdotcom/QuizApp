package com.example.quizzapp.security;

import com.example.quizzapp.model.User;
import com.example.quizzapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import com.example.quizzapp.model.UserRole;
import java.util.*;

@Service
@Primary
public class CustomerUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // UserRole stored as enum in user entity
        UserRole userRole = user.getRole();

        Collection<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + userRole.name())
        );

        return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                userRole,
                user.getPassword(),
                authorities
        );
    }
}