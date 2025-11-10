// java
package com.example.quizzapp.service;

import com.example.quizzapp.model.User;
import com.example.quizzapp.repository.UserRepository;
import com.example.quizzapp.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import com.example.quizzapp.model.UserRole;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Service
@Primary
public class CustomerUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOpt = userRepository.findByUsername(username);

        User user = userOpt.orElseThrow(() ->
                new UsernameNotFoundException("User not found: " + username)
        );

        // If role is null, fall back to STUDENT to avoid NPEs
        UserRole userRole = user.getRole() != null ? user.getRole() : UserRole.STUDENT;

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
