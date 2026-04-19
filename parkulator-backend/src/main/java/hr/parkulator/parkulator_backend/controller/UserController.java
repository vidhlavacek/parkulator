package hr.parkulator.parkulator_backend.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hr.parkulator.parkulator_backend.services.UserService;
import hr.parkulator.parkulator_backend.dto.user.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/current")
    public UserResponseDTO getCurrentUser(Authentication authentication) {
        return userService.getUserByEmail(authentication.getName());
    }

    @PutMapping("/username")
    public UserResponseDTO updateUsername(@RequestBody UpdateUsernameDTO dto, Authentication auth) {
        return userService.updateUsername(auth.getName(), dto.getUsername());
    }

    @PutMapping("/email")
    public UserResponseDTO updateEmail(@RequestBody UpdateEmailDTO dto, Authentication auth) {
        return userService.updateEmail(auth.getName(), dto.getEmail());
    }

    @PutMapping("/password")
    public void updatePassword(@RequestBody UpdatePasswordDTO dto, Authentication auth) {
        userService.updatePassword(
                auth.getName(),
                dto.getOldPassword(),
                dto.getNewPassword()
        );
    }
}
