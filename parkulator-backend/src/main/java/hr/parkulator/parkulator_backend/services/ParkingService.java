package hr.parkulator.parkulator_backend.services;

import org.springframework.stereotype.Service;
import java.util.List;

import hr.parkulator.parkulator_backend.repositories.ParkingRepository;
import lombok.AllArgsConstructor;
import hr.parkulator.parkulator_backend.entities.Parking;

@Service
@AllArgsConstructor
public class ParkingService {
    private ParkingRepository parkingRepository;

    public List<Parking> getAllParkings(){
        return parkingRepository.findAll();
    }

    public Parking getParkingById(Long id) {
        return parkingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parking not found"));
    }

    public Parking getParkingByName(String name) {
        return parkingRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Parking not found"));
    }

    public Parking getParkingByAddress(String address) {
        return parkingRepository.findByAddress(address)
                .orElseThrow(() -> new RuntimeException("Parking not found"));
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
