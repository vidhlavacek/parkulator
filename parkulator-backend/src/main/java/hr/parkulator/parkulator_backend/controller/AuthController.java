package hr.parkulator.parkulator_backend.controller;

import hr.parkulator.parkulator_backend.dto.auth.AuthResponseDTO;
import hr.parkulator.parkulator_backend.dto.auth.LoginRequestDTO;
import hr.parkulator.parkulator_backend.dto.auth.RegistrationRequestDTO;
import hr.parkulator.parkulator_backend.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
     private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegistrationRequestDTO request) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(authService.login(request));
    }
}
