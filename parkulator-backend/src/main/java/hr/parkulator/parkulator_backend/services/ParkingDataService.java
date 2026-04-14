package hr.parkulator.parkulator_backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hr.parkulator.parkulator_backend.dto.ParkingDataDTO;
import hr.parkulator.parkulator_backend.repositories.ParkingRepository;
import hr.parkulator.parkulator_backend.entities.Parking;
import hr.parkulator.parkulator_backend.dto.ParkingPriceDTO;
import hr.parkulator.parkulator_backend.entities.ParkingPrice;

@Service
public class ParkingDataService {
    @Autowired
    private ParkingRepository parkingRepository;
    @Autowired
    private LiveParkingDataService liveParkingDataService;
    @Autowired
    private StaticParkingDataService staticParkingDataService;

    @Transactional
    public void saveInitialData(){
        List<ParkingDataDTO> dtos = liveParkingDataService.getInitialRijekaPlusData();
        dtos.addAll(staticParkingDataService.getInitialStaticParkingData());

        for(ParkingDataDTO dto : dtos){
            Parking parking = new Parking();
            parking.setExternalId(dto.getExternalId());
            parking.setName(dto.getName());
            parking.setAddress(dto.getAddress());
            parking.setLink(dto.getLink());
            parking.setLive(dto.isLive());
            parking.setType(dto.getType());
            parking.setSpots(dto.getSpots());
            parking.setAvailableSpots(dto.getAvailableSpots());
            List<ParkingPriceDTO> pps = dto.getParkingPrice();
            for(ParkingPriceDTO price : pps){
                ParkingPrice parkingPrice = new ParkingPrice();
                
                parkingPrice.setDay(price.getDay());
                parkingPrice.setSpecial(price.getSpecial());
                parkingPrice.setOpeningHour(price.getOpeningHour());
                parkingPrice.setClosingHour(price.getClosingHour());
                parkingPrice.setPrice(price.getPrice());
                
                parking.addPrice(parkingPrice);
            }
            parkingRepository.save(parking);
        }
    }
    
    @Transactional
    public void saveRefreshData(){
        
    }
}
