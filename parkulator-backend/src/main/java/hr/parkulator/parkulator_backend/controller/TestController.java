package hr.parkulator.parkulator_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import hr.parkulator.parkulator_backend.services.ParkingDataService;

@RestController
public class TestController {
    ParkingDataService parkingDataService;

    public TestController(ParkingDataService parkingDataService) {
        this.parkingDataService = parkingDataService;
    }

    @GetMapping("/test-live-data")
    public String testLiveData() {
        parkingDataService.saveInitialData();
        return "POSLANO";
    }
}