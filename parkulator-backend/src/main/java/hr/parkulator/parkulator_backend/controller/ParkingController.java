package hr.parkulator.parkulator_backend.controller;

import org.springframework.web.bind.annotation.*;

import hr.parkulator.parkulator_backend.dto.parking.ParkingSearchResponseDTO;
import hr.parkulator.parkulator_backend.entities.Parking;
import hr.parkulator.parkulator_backend.services.ParkingServices.ParkingService;




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
    public ParkingSearchResponseDTO getParkings(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Double maxDistance,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            @RequestParam(required = false)Double maxPrice
    ) {
       return parkingService.getFilteredParkings(type, maxPrice, maxDistance, lat, lng);
    }    
}