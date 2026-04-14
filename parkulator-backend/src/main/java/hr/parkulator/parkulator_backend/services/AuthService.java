package hr.parkulator.parkulator_backend.services;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import lombok.RequiredArgsConstructor;

import hr.parkulator.parkulator_backend.repositories.UserRepository;
import hr.parkulator.parkulator_backend.dto.AuthResponseDTO;
import hr.parkulator.parkulator_backend.dto.LoginRequestDTO;
import hr.parkulator.parkulator_backend.dto.RegistrationRequestDTO;
import hr.parkulator.parkulator_backend.entities.User;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponseDTO  register(RegistrationRequestDTO request){
        if(userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        if(userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
        }

        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword())).build();

        User savedUser = userRepository.save(user);

        return AuthResponseDTO.builder().id(savedUser.getId()).email(savedUser.getEmail()).username(savedUser.getUsername()).build();
    }

    public AuthResponseDTO login(LoginRequestDTO request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid password");
        }

        return AuthResponseDTO.builder().id(user.getId()).email(user.getEmail()).username(user.getUsername()).build();
    }
}
