package hr.parkulator.parkulator_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StaticParkingDataDTO {
    private String address;
    private String zone;
    private String price;

}
