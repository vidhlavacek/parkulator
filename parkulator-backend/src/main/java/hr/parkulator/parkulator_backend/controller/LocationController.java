package hr.parkulator.parkulator_backend.controller;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import hr.parkulator.parkulator_backend.dto.LocationLogDTO;
import hr.parkulator.parkulator_backend.services.Telemetry.LocationLogService;

@RestController
@AllArgsConstructor
public class LocationController {

    private final LocationLogService locationLogService;

    @PostMapping("/location")
    public ResponseEntity<Void> recieveLocation(@RequestBody LocationLogDTO location) {
        locationLogService.filterLocationData(location);
        return ResponseEntity.ok().build();
    }
    
}