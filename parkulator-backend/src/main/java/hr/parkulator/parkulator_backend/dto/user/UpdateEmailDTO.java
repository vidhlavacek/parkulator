package hr.parkulator.parkulator_backend.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEmailDTO {
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email format is not valid")
    private String email;
}
