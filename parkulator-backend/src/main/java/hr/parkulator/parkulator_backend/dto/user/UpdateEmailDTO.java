package hr.parkulator.parkulator_backend.dto.user;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEmailDTO {
    private String email;
}
