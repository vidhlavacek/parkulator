package hr.parkulator.parkulator_backend.config;

import org.springframework.http.HttpMethod;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import hr.parkulator.parkulator_backend.repositories.UserRepository;
import hr.parkulator.parkulator_backend.entities.User;
import hr.parkulator.parkulator_backend.security.JwtAuthenticationEntryPoint;
import hr.parkulator.parkulator_backend.security.JwtFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //Main configuration for authentication
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtFilter jwtFilter, JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) throws Exception {
        http
            //Disable CSRF
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/parkings/**").permitAll()
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/location/**").permitAll()
                .requestMatchers("/favorites/**").authenticated()
                .requestMatchers("/history/**").authenticated()
                .requestMatchers("/users/**").authenticated()
                .anyRequest().authenticated()
            )
            //Add JWT filter before authentication filter
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

     //Loads user for authentication
    @Bean
    public UserDetailsService userDetailsService(UserRepository repo) {
        return email -> {
            User user = repo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getEmail())
                    .password(user.getPassword())
                    .build();
        };
    }
}