package hr.parkulator.parkulator_backend.services;

import hr.parkulator.parkulator_backend.exception.ConflictException;
import hr.parkulator.parkulator_backend.exception.UnauthorizedException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import hr.parkulator.parkulator_backend.repositories.UserRepository;
import hr.parkulator.parkulator_backend.dto.auth.AuthResponseDTO;
import hr.parkulator.parkulator_backend.dto.auth.LoginRequestDTO;
import hr.parkulator.parkulator_backend.dto.auth.RegistrationRequestDTO;
import hr.parkulator.parkulator_backend.entities.User;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    public AuthResponseDTO  register(RegistrationRequestDTO request){
        if(userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ConflictException("Email already exists");
        }

        if(userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ConflictException("Username already exists");
        }

        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword())).build();

        User savedUser = userRepository.save(user);

        String token = jwtService.generateToken(savedUser);

        return AuthResponseDTO.builder()
            .id(savedUser.getId())
            .email(savedUser.getEmail())
            .username(savedUser.getUsername())
            .token(token)
            .build();
    }

    public AuthResponseDTO login(LoginRequestDTO request){
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        String token = jwtService.generateToken(user);

        return AuthResponseDTO.builder()
            .id(user.getId())
            .email(user.getEmail())
            .username(user.getUsername())
            .token(token)
            .build();
    }
}
