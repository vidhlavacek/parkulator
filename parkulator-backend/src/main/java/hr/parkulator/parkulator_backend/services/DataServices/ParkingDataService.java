package hr.parkulator.parkulator_backend.services.DataServices;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import hr.parkulator.parkulator_backend.repositories.ParkingRepository;
import hr.parkulator.parkulator_backend.entities.Parking;
import hr.parkulator.parkulator_backend.dto.parking.ParkingDataDTO;
import hr.parkulator.parkulator_backend.dto.parking.ParkingPriceDTO;
import hr.parkulator.parkulator_backend.dto.parking.ParkingRefreshDTO;
import hr.parkulator.parkulator_backend.entities.ParkingPrice;
import hr.parkulator.parkulator_backend.dto.parking.ParkingLocationDTO;



@Slf4j
@Service
@RequiredArgsConstructor
public class ParkingDataService {

    @Autowired
    private ParkingRepository parkingRepository;
    @Autowired
    private LiveParkingDataService liveParkingDataService;
    @Autowired
    private StaticParkingDataService staticParkingDataService;
    @Autowired
    private ParkingLocationDataService parkingLocationDataService;

    private final ObjectMapper om;

    @Transactional
    public void saveInitialData(){
        //Saving initial Parking data with LiveParkingDataService and StaticParkingDataService
        //Run when the backend application is started

        //Getting the data
        List<ParkingDataDTO> dtos = liveParkingDataService.getInitialRijekaPlusData();
        dtos.addAll(staticParkingDataService.getInitialStaticParkingData());

        //Creating list of Parking entities for the database
        for(ParkingDataDTO dto : dtos){
            //Check if a parking already exists
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
            parking.setLatitude(dto.getLatitude());
            parking.setLongitude(dto.getLongitude());
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
            //Save to the database
            parkingRepository.save(parking);
        }
        saveParkingLocationData();
    }
    
    @Transactional
    public void saveRefreshData(){
        //Refreshing Parking data in the database
        //Scheduler runs this method

        //Getting the data
        List<ParkingRefreshDTO> pr = liveParkingDataService.refreshRijekaPlusData();
        //pr.addAll(staticParkingDataService.getRefreshStaticParkingData());

        //Updateing each Parking (if it exists)
        for(ParkingRefreshDTO RefreshData : pr){
            Parking parking = parkingRepository
                .findBySourceKey(RefreshData.getSourceKey())
                .orElseThrow(() -> new RuntimeException("Parking not found" + RefreshData.getName() + RefreshData.getSourceKey()));

            parking.setName(RefreshData.getName());
            parking.setLive(RefreshData.isLive());
            parking.setAvailableSpots(RefreshData.getAvailableSpots());
            
            //Clearing all prices in a parking in case there has been a big change in data
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

    private void saveParkingLocationData(){
        List<Parking> parkings = parkingRepository.findByLatitudeIsNullOrLongitudeIsNull();

        if(parkings.isEmpty()){
            log.info("All parkings have longitude and latitude");
            return;
        }
        for(Parking p : parkings){
            try{
                ParkingLocationDTO parkingLocation;
                boolean cached_flag = false;

                parkingLocation = getLocationFromJson(p.getSourceKey());
                if(parkingLocation != null){
                    cached_flag = true;
                }
                
                if(!cached_flag) parkingLocation = parkingLocationDataService.getParkingLocation(p.getName(), p.getAddress());

                if(parkingLocation != null){
                p.setLatitude(parkingLocation.latitude());
                p.setLongitude(parkingLocation.longitude());

                parkingRepository.save(p);

                if(!cached_flag) saveLocationToJson(p.getSourceKey(), parkingLocation.longitude(), parkingLocation.latitude());
                log.info("Saved " + p.getSourceKey());
                }
                else{
                    log.warn("Parking location failed. Skipping " + p.getSourceKey());
                }

                Thread.sleep(1100);
            }
            catch(InterruptedException e){
                Thread.currentThread().interrupt();
                log.error("Thread interrupted while waiting between Nominatim requests", e);
                return;
            }
            catch(Exception e){
                log.warn("[ParkingDataService] Parking location failed", e);
            }
        }
    }

    private void saveLocationToJson(String sourceKey, Double longitude, Double latitude){
        try {
        Path filePath = Paths.get("data", "parking-locations.json");

        if (Files.notExists(filePath.getParent())) {
            Files.createDirectories(filePath.getParent());
        }

        ArrayNode locationsArray;

        if (Files.exists(filePath) && Files.size(filePath) > 0) {
            JsonNode existingJson = om.readTree(filePath.toFile());

            if (existingJson != null && existingJson.isArray()) {
                locationsArray = (ArrayNode) existingJson;
            } else {
                locationsArray = om.createArrayNode();
            }
        } else {
            locationsArray = om.createArrayNode();
        }

        ObjectNode parkingNode = om.createObjectNode();
        parkingNode.put("sourceKey", sourceKey);
        parkingNode.put("longitude", longitude);
        parkingNode.put("latitude", latitude);

        locationsArray.add(parkingNode);

        om.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), locationsArray);

        log.info("Parking written to JSON: {}", sourceKey);

    } catch (Exception e) {
        log.error("Failed to save parking to JSON: {}", sourceKey, e);
    }
    }

    private ParkingLocationDTO getLocationFromJson(String sourceKey){
        try {
        Path filePath = Paths.get("data", "parking-locations.json");

        if (Files.notExists(filePath) || Files.size(filePath) == 0) {
            return null;
        }

        JsonNode root = om.readTree(filePath.toFile());

        if (root == null || !root.isArray()) {
            return null;
        }

        for (JsonNode node : root) {
            String jsonSourceKey = node.path("sourceKey").asString();

            if (sourceKey.equals(jsonSourceKey)) {
                Double latitude = node.path("latitude").asDouble();
                Double longitude = node.path("longitude").asDouble();

                return new ParkingLocationDTO(latitude, longitude);
            }
        }

    } catch (Exception e) {
        log.error("Failed to read parking location from JSON: {}", sourceKey, e);
        return null;
    }
        
        
        
        return null; 
    }
}
