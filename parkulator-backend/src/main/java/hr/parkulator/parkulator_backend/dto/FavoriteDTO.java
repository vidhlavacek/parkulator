package hr.parkulator.parkulator_backend.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteDTO {

    private Long parkingId;
    private String name;
    private String address;
    private String type;
}