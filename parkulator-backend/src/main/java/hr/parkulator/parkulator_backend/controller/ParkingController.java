package hr.parkulator.parkulator_backend.controller;

import hr.parkulator.parkulator_backend.dto.parking.ParkingDTO;
import hr.parkulator.parkulator_backend.dto.parking.ParkingSearchResponseDTO;
import hr.parkulator.parkulator_backend.entities.Parking;
import hr.parkulator.parkulator_backend.services.DataServices.ParkingDataService;
import hr.parkulator.parkulator_backend.services.ParkingServices.ParkingService;

import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/parkings")
public class ParkingController {

    private final ParkingService parkingService;
    private final ParkingDataService parkingDataService;

    public ParkingController(ParkingService parkingService, ParkingDataService parkingDataService) {
        this.parkingService = parkingService;
        this.parkingDataService = parkingDataService;
    }

    @GetMapping("/{id}")
    public Parking getById(@PathVariable Long id) {
        return parkingService.getParkingById(id);
    }

    @GetMapping
    public ParkingSearchResponseDTO getParkings(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Double maxDistance,
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(required = false)Double maxPrice
    ) {
        return parkingService.getFilteredParkings(type, maxDistance, lat, lng, maxPrice);
    }

    //temporary route for testing purposes, will be removed later
    @GetMapping("/all")
    public List<ParkingDTO> getAllParkingLots(){
        return parkingService.getAllParkings();
    }

    @GetMapping("/refresh")
    public String refreshParkingLots() {
        parkingDataService.saveRefreshData();
        return "DONE";
    }
    
    
}