package hr.parkulator.parkulator_backend.services.DataServices;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hr.parkulator.parkulator_backend.repositories.ParkingRepository;
import hr.parkulator.parkulator_backend.entities.Parking;
import hr.parkulator.parkulator_backend.dto.parking.ParkingDataDTO;
import hr.parkulator.parkulator_backend.dto.parking.ParkingPriceDTO;
import hr.parkulator.parkulator_backend.dto.parking.ParkingRefreshDTO;
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
            if(parkingRepository.findBySourceKey(dto.getSourceKey()).isPresent()) continue;
            Parking parking = new Parking();
            parking.setSourceKey(dto.getSourceKey());
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
        List<ParkingRefreshDTO> pr = liveParkingDataService.refreshRijekaPlusData();
        //pr.addAll(staticParkingDataService.getRefreshStaticParkingData());

        for(ParkingRefreshDTO RefreshData : pr){
            Parking parking = parkingRepository
                .findBySourceKey(RefreshData.getSourceKey())
                .orElseThrow(() -> new RuntimeException("Parking not found" + RefreshData.getName() + RefreshData.getSourceKey()));

            parking.setSourceKey(RefreshData.getSourceKey());
            parking.setName(RefreshData.getName());
            parking.setLive(RefreshData.isLive());
            parking.setAvailableSpots(RefreshData.getAvailableSpots());
            
            parking.getParkingPrices().clear();
            List<ParkingPriceDTO> ppDto = RefreshData.getParkingPrice();
            for(ParkingPriceDTO pp : ppDto){
                ParkingPrice parkingPrice = new ParkingPrice();
                
                parkingPrice.setDay(pp.getDay());
                parkingPrice.setSpecial(pp.getSpecial());
                parkingPrice.setOpeningHour(pp.getOpeningHour());
                parkingPrice.setClosingHour(pp.getClosingHour());
                parkingPrice.setPrice(pp.getPrice());
                
                parking.addPrice(parkingPrice);
            }
        }
    }
}
