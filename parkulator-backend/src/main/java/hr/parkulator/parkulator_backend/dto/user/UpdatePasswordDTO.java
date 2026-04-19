package hr.parkulator.parkulator_backend.dto.user;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordDTO {
    private String oldPassword;
    private String newPassword;
}
