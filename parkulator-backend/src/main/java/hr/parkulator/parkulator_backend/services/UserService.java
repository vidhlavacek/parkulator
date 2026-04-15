package hr.parkulator.parkulator_backend.services;

import hr.parkulator.parkulator_backend.dto.user.UserResponseDTO;
import hr.parkulator.parkulator_backend.entities.User;
import hr.parkulator.parkulator_backend.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public List<UserResponseDTO> getEveryUser(){
        return userRepository.findAll().stream().map(user -> UserResponseDTO.builder()
            .id(user.getId())
            .email(user.getEmail())
            .username(user.getUsername()).build()
        ).collect(Collectors.toList());
    }

    public User getUserById(Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    public User getUserByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
    }

    public User saveUser(User user){
        return userRepository.save(user);
    }

    public void deleteUser(Long id){
        userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        userRepository.deleteById(id);
    }
}
