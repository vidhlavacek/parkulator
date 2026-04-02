package hr.parkulator.parkulator_backend.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import hr.parkulator.parkulator_backend.services.LiveParkingDataService;

@RestController
public class TestController {

    private final LiveParkingDataService liveParkingDataService;

    public TestController(LiveParkingDataService liveParkingDataService) {
        this.liveParkingDataService = liveParkingDataService;
    }

    @GetMapping("/test-live-data")
    public String testLiveData() {
        return liveParkingDataService.test();
    }
}