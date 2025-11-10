package com.example.quizzapp.config;

import com.example.quizzapp.security.AuthTokenFilter;
import com.example.quizzapp.security.AuthenticationFailureHandlerImpl;
import com.example.quizzapp.security.AuthenticationSuccessHandlerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired(required = false)
    private AuthTokenFilter authTokenFilter;

    @Autowired
    private AuthenticationSuccessHandlerImpl authenticationSuccessHandlerImpl;

    @Autowired
    private AuthenticationFailureHandlerImpl authenticationFailureHandlerImpl;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // disabling CSRF for simplicity (templates can be updated to include CSRF token if desired)
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        // use the project's auth login template
                        .loginPage("/auth/login")
                        // ensure Spring Security processes the login request at the same URL as the form posts to
                        .loginProcessingUrl("/auth/login")
                        .successHandler(authenticationSuccessHandlerImpl)
                        .failureHandler(authenticationFailureHandlerImpl)
                        .permitAll()
                )

                .logout(logout -> logout.permitAll());

        // Register JWT filter if present
        if (authTokenFilter != null) {
            http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);
        }

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
