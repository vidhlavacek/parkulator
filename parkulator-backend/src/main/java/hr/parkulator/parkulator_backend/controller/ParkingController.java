package hr.parkulator.parkulator_backend.controller;

import hr.parkulator.parkulator_backend.entities.Parking;
import hr.parkulator.parkulator_backend.services.ParkingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/parkings")
public class ParkingController {

    private final ParkingService parkingService;

    public ParkingController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @GetMapping("/{id}")
    public Parking getById(@PathVariable Long id) {
        return parkingService.getParkingById(id);
    }

    @GetMapping
    public List<Parking> getParkings(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Double maxDistance,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng
    ) {
        return parkingService.getFilteredParkings(type, maxPrice, maxDistance, lat, lng);
    }
    
}