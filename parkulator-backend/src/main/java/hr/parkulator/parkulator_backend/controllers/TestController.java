package hr.parkulator.parkulator_backend.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

import hr.parkulator.parkulator_backend.services.LiveParkingDataService;
import hr.parkulator.parkulator_backend.services.StaticParkingDataService;
import hr.parkulator.parkulator_backend.dto.LiveParkingRefreshDTO;

@RestController
public class TestController {

    private final LiveParkingDataService liveParkingDataService;
    private final StaticParkingDataService staticParkingDataService;

    public TestController(LiveParkingDataService liveParkingDataService, StaticParkingDataService staticParkingDataService) {
        this.liveParkingDataService = liveParkingDataService;
        this.staticParkingDataService = staticParkingDataService;
    }

    @GetMapping("/test-live-data")
    public List<LiveParkingRefreshDTO> testLiveData() {
        return liveParkingDataService.refreshRijekaPlusData();
    }
    /*
    @GetMapping("/test-static-data")
    public List<StaticParkingDataDTO> testStaticData() {
        return staticParkingDataService.RijekaPlusScraper();
    }
    */
}