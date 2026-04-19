package hr.parkulator.parkulator_backend.dto.user;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUsernameDTO {
    private String username;
}
