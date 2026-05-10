package hr.parkulator.parkulator_backend.services;

import hr.parkulator.parkulator_backend.dto.user.UserResponseDTO;
import hr.parkulator.parkulator_backend.entities.User;
import hr.parkulator.parkulator_backend.exception.ConflictException;
import hr.parkulator.parkulator_backend.exception.ResourceNotFoundException;
import hr.parkulator.parkulator_backend.exception.UnauthorizedException;
import hr.parkulator.parkulator_backend.repositories.UserRepository;
import lombok.AllArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public List<UserResponseDTO> getEveryUser(){
        return userRepository.findAll().stream().map(user -> UserResponseDTO.builder()
            .id(user.getId())
            .email(user.getEmail())
            .username(user.getUsername()).build()
        ).collect(Collectors.toList());
    }

    public User getUserById(Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + "not found"));
    }

    public UserResponseDTO getUserByEmail(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with email: " + email + "not found"));

        return UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .build();
    }

    public User saveUser(User user){
        return userRepository.save(user);
    }

    public void deleteUser(Long id){
        userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + "not found"));
        userRepository.deleteById(id);
    }

    public UserResponseDTO updateUsername(String email, String newUsername) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with email: " + email + " not found"));

        user.setUsername(newUsername);
        userRepository.save(user);

        return UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .build();
    }

    public UserResponseDTO updateEmail(String currentEmail, String newEmail) {
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User with email: " + currentEmail + " not found"));

        if (userRepository.findByEmail(newEmail).isPresent()) {
            throw new ConflictException("Email already in use");
        }

        user.setEmail(newEmail);
        userRepository.save(user);

        return UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .build();
    }

    public void updatePassword(String email, String oldPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with email: " + email + " not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new UnauthorizedException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
