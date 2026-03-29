package hr.parkulator.parkulator_backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

import hr.parkulator.parkulator_backend.repositories.ParkingRepository;
import lombok.AllArgsConstructor;
import hr.parkulator.parkulator_backend.entities.Parking;

@Service
@AllArgsConstructor
public class ParkingService {
    @Autowired
    private ParkingRepository parkingRepository;

    public List<Parking> getAllParkings(){
        return parkingRepository.findAll();
    }
}
