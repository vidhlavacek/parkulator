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
        //When the application is run getting the inital data (if needed) for the database
        parkingDataService.saveInitialData();
    }
}
