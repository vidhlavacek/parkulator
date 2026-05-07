package hr.parkulator.parkulator_backend.dto.parking;

import lombok.Builder;

@Builder
public record ParkingLocationDTO(Double latitude, Double longitude) {
} 
    
