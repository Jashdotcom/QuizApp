package com.example.quizzapp.config;

import com.example.quizzapp.service.CustomerUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomerUserDetailsService customerUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * ✅ Define the AuthenticationManager explicitly using AuthenticationManagerBuilder
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.userDetailsService(customerUserDetailsService).passwordEncoder(passwordEncoder());
        return authBuilder.build();
    }

    /**
     * ✅ Configure your security filter chain
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/teacher/quizzes/*/publish", "/teacher/quizzes/*/unpublish")
                )
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/", "/auth/**", "/register", "/css/**", "/js/**").permitAll()
                        .requestMatchers("/dashboard").authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/auth/login?logout")
                        .permitAll()
                );

        return http.build();
    }



    /**
     * ✅ DaoAuthenticationProvider if you use it somewhere
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customerUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}
