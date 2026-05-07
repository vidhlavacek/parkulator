package hr.parkulator.parkulator_backend.services.DataServices;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import hr.parkulator.parkulator_backend.dto.parking.ParkingDataDTO;
import hr.parkulator.parkulator_backend.dto.parking.ParkingRefreshDTO;

import org.springframework.stereotype.Service;
import org.springframework.core.io.ClassPathResource;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StaticParkingDataService {
    
    public List<ParkingDataDTO> getInitialStaticParkingData(){
        try{
            log.info("[STATIC INITIAL PARKING DATA] Starting...");

            InputStream inputStream = new ClassPathResource("data/parkings.json").getInputStream();

            ObjectMapper om = new ObjectMapper();

            JsonNode root = om.readTree(inputStream);

            List<ParkingDataDTO> result = new ArrayList<>();
            
            for (JsonNode node : root) {
                String externalId = node.path("externalId").asString("");
                String name = node.path("name").asString("");
                String address = node.path("address").asString("");

                String sourceKey = createSourceKey(externalId, name, address);

                ParkingDataDTO dto = om.treeToValue(node, ParkingDataDTO.class);
                dto.setSourceKey(sourceKey);
                result.add(dto);
            }

        log.info("[STATIC INITIAL PARKING DATA] SUCCESS");
        return result;
        } catch(Exception e){
            log.warn("[STATIC INITIAL PARKING DATA] Failed, can't open file");
            return new ArrayList<>();
        }
        
    }

    public List<ParkingRefreshDTO> getRefreshStaticParkingData(){
        try{
            log.info("[STATIC REFRESH PARKING DATA] Starting...");

            InputStream inputStream = new ClassPathResource("data/parkingsUpdate.json").getInputStream();
            ObjectMapper om = new ObjectMapper();
             JsonNode root = om.readTree(inputStream);

            List<ParkingRefreshDTO> result = new ArrayList<>();
            
            for (JsonNode node : root) {
                String externalId = node.path("externalId").asString("");
                String name = node.path("name").asString("");
                String address = node.path("address").asString("");

                String sourceKey = createSourceKey(externalId, name, address);

                ParkingRefreshDTO dto = om.treeToValue(node, ParkingRefreshDTO.class);
                dto.setSourceKey(sourceKey);
                result.add(dto);
        }
        log.info("[STATIC REFRESH PARKING DATA] SUCCESS");
        return result;
        } catch(Exception e){
            log.warn("[STATIC REFRESH PARKING DATA] Failed, can't open file");
            return new ArrayList<>();
        }
    }

    public String createSourceKey(String externalId, String name, String address){
        return externalId.toLowerCase().trim() + "|" + name.toLowerCase().trim() + "|" + address.toLowerCase().trim();
    }
    
}

