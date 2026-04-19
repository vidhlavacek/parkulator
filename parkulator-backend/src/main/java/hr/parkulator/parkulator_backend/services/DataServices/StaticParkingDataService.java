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

@Service
public class StaticParkingDataService {
    
    public List<ParkingDataDTO> getInitialStaticParkingData(){
        try{
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

        return result;
        } catch(Exception e){
            throw new RuntimeException("Can't open parking.json", e);
        }
        
    }

    public List<ParkingRefreshDTO> getRefreshStaticParkingData(){
        try{
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

        return result;
        } catch(Exception e){
            throw new RuntimeException("Can't open parkingsUpdate.json", e);
        }
    }

    public String createSourceKey(String externalId, String name, String address){
        return externalId.toLowerCase().trim() + "|" + name.toLowerCase().trim() + "|" + address.toLowerCase().trim();
    }
    
}

