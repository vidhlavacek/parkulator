package hr.parkulator.parkulator_backend.controller;

import hr.parkulator.parkulator_backend.dto.auth.AuthResponseDTO;
import hr.parkulator.parkulator_backend.dto.auth.LoginRequestDTO;
import hr.parkulator.parkulator_backend.dto.auth.RegistrationRequestDTO;
import hr.parkulator.parkulator_backend.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
     private final AuthService authService;

    @PostMapping("/register")
    public AuthResponseDTO register(@Valid @RequestBody RegistrationRequestDTO request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponseDTO login(@Valid @RequestBody LoginRequestDTO request) {
        return authService.login(request);
    }
}
