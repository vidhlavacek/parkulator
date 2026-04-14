package hr.parkulator.parkulator_backend.dto;

import hr.parkulator.parkulator_backend.shared.WorkDayEnum;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingPriceDTO {
    private WorkDayEnum day;
    private String special;
    private int openingHour;
    private int closingHour;
    private double price;
}
