package hr.parkulator.parkulator_backend.services.ParkingServices;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.stereotype.Service;

import hr.parkulator.parkulator_backend.dto.parking.ParkingDTO;
import hr.parkulator.parkulator_backend.entities.Parking;
import hr.parkulator.parkulator_backend.entities.ParkingPrice;
import hr.parkulator.parkulator_backend.exception.ResourceNotFoundException;
import hr.parkulator.parkulator_backend.shared.WorkDayEnum;

@Service
public class ParkingMapperService {

    public ParkingDTO mapToDTO(Parking parking){
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        WorkDayEnum workDay = resolveWorkDay(today.getDayOfWeek());

        return mapToDTO(parking, now, workDay);
    }

    public ParkingDTO mapToDTO( Parking parking, LocalTime now, WorkDayEnum workDay){

        if (parking.getLatitude() == null || parking.getLongitude() == null) {
            throw new ResourceNotFoundException("Parking " + parking.getName() + " missing coordinates.");
        }

        ParkingDTO dto = new ParkingDTO();
        dto.setId(parking.getId());
        dto.setName(parking.getName());
        dto.setAddress(parking.getAddress());
        dto.setType(parking.getType());
        dto.setLink(parking.getLink());
        dto.setLive(parking.isLive());
        dto.setSpots(parking.getSpots());
        dto.setAvailableSpots(parking.getAvailableSpots());
        dto.setLatitude(parking.getLatitude());
        dto.setLongitude(parking.getLongitude());
        dto.setOccupancyStatus(parking.getOccupancyStatus());

        StringBuilder message = new StringBuilder();

        double price = getCurrentPrice(parking, now, workDay, message);
        dto.setPrice(price);
        dto.setParkingStatus(message.length() == 0 ? null : message.toString());

        return dto;
    }

    private double getCurrentPrice(Parking parking, LocalTime now, WorkDayEnum workDay, StringBuilder message){

        if (parking.getParkingPrices() == null || parking.getParkingPrices().isEmpty()){
            message.append("No pricing data available");
            return 0.0;
        }

        int currentHour = now.getHour();

        boolean hasRuleForToday = parking.getParkingPrices()
                .stream()
                .anyMatch(rule -> rule.getDay() == workDay || rule.getDay() == WorkDayEnum.ALLDAYS);

        boolean isInRange = false;

        for (ParkingPrice rule : parking.getParkingPrices()){
            if (rule.getPrice() < 0) continue;
            if (rule.getOpeningHour() < 0 || rule.getOpeningHour() > 24) continue;
            if (rule.getClosingHour() < 0 || rule.getClosingHour() > 24) continue;

            if (rule.getDay() == WorkDayEnum.SPECIAL) continue;

            // wrong day
            if (rule.getDay() != workDay && rule.getDay() != WorkDayEnum.ALLDAYS){
                continue;
            }

            int open = rule.getOpeningHour();
            int close = rule.getClosingHour();

            boolean inRange = (open <= close && currentHour >= open && currentHour <= close)
                                || (open > close && (currentHour >= open || currentHour <= close));

            if ((open == 0 && close == 0) || inRange){
                isInRange = true;
                return rule.getPrice();
            }
        }

        if (!hasRuleForToday || isInRange == false) {
            message.append("No pricing information available. Parking may be free or closed.");
        }

        return 0.0;
    }

    private WorkDayEnum resolveWorkDay(DayOfWeek day) {
        return switch (day) {
            case SATURDAY -> WorkDayEnum.SATURDAY;
            case SUNDAY -> WorkDayEnum.SUNDAY;
            default -> WorkDayEnum.WORKDAY;
        };
    }
}
