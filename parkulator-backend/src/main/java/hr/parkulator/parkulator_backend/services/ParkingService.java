package hr.parkulator.parkulator_backend.services;

import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import hr.parkulator.parkulator_backend.repositories.ParkingRepository;
import hr.parkulator.parkulator_backend.shared.WorkDayEnum;
import hr.parkulator.parkulator_backend.entities.ParkingPrice;
import hr.parkulator.parkulator_backend.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import hr.parkulator.parkulator_backend.dto.parking.ParkingDTO;
import hr.parkulator.parkulator_backend.entities.Parking;

@Service
@AllArgsConstructor
public class ParkingService {
    private ParkingRepository parkingRepository;

    public Parking getParkingById(Long id) {
        return parkingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parking with id " + id + " not found"));
    }

    public Parking getParkingByName(String name) {
        return parkingRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Parking with name " + name + " not found"));
    }

    public Parking getParkingByAddress(String address) {
        return parkingRepository.findByAddress(address)
                .orElseThrow(() -> new ResourceNotFoundException("Parking with address " + address + " not found"));
    }

    public List<Parking> getParkingByType(String type) {
        return parkingRepository.findByType(type);
    }

   public List<Parking> getFilteredParkings(
        String type,
        Double maxPrice,
        Double maxDistance,
        Double lat,
        Double lng
    ) {
        return parkingRepository.filterAll(type, maxPrice, maxDistance, lat, lng);
    }
}

