package hr.parkulator.parkulator_backend.seed;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import hr.parkulator.parkulator_backend.services.DataServices.ParkingDataService;

@Component
@RequiredArgsConstructor
public class ParkingDataInitializer implements CommandLineRunner {
    private final ParkingDataService parkingDataService;

    @Override
    public void run(String... args){
        parkingDataService.saveInitialData();
    }
}
