package hr.parkulator.parkulator_backend.services;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import hr.parkulator.parkulator_backend.dto.ParkingDataDTO;
import hr.parkulator.parkulator_backend.repositories.ParkingRepository;
import hr.parkulator.parkulator_backend.entities.Parking;
import hr.parkulator.parkulator_backend.dto.ParkingPriceDTO;
import hr.parkulator.parkulator_backend.entities.ParkingPrice;

public class ParkingDataService {
    private ParkingRepository parkingRepository;
    private LiveParkingDataService liveParkingDataService;

    @Transactional
    public void toDatabase(){
        List<ParkingDataDTO> dtos = liveParkingDataService.getInitialRijekaPlusData();
        //Add list of static objects

        for(ParkingDataDTO dto : dtos){
            Parking parking = new Parking();
            //parking.set|varijabla|(dto.get|varijabla|)
            List<ParkingPriceDTO> pps = dto.getParkingPrice();
            for(ParkingPriceDTO price : pps){
                ParkingPrice parkingPrice = new ParkingPrice();
                price.getClass();
                parkingPrice.getClass();
                //parkingPrice.set|varijabla|(price.get|varijabla|)
                //parking.addPrice(parkingPrice)
            }
            parkingRepository.save(parking);
        }
    }
}
