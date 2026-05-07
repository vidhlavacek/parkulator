package hr.parkulator.parkulator_backend.services.DataServices;

import hr.parkulator.parkulator_backend.dto.parking.ParkingLocationDTO;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ParkingLocationDataService {
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public ParkingLocationDataService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder()
            .baseUrl("https://nominatim.openstreetmap.org")
            .defaultHeader("User-Agent", "Parkulator/1.0, student project")
            .build();
    }

    public ParkingLocationDTO getParkingLocation(String name, String address){
        //Getting longitude and latitude of a parking lot by its name and address
        try {
            String query;
            String place = ", Rijeka, Croatia";

            //Creating a query with regard to inconsistencies in the database :)
            if(name == null || name.toLowerCase().contains("zona")){
                query = address + place; 
            }
            else if(address == null || address.isBlank()){
                query = name + place;
            }
            else{
                query = name + ", " + address + place;
            }
            log.info("[ParkingLocationDataService] Getting location of" + query);


            String response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("format", "jsonv2")
                        .queryParam("limit", 1)
                        .queryParam("countrycodes", "hr")
                        .queryParam("addressdetails", 1)
                        .queryParam("accept-language", "hr,en")
                        .queryParam("q", query)
                        .build())
                .retrieve()
                .body(String.class);

            JsonNode root = objectMapper.readTree(response);

            if(!root.isArray() || root.isEmpty()) throw new Exception();

            Double latitude = Double.parseDouble(root.get(0).get("lat").asString());
            Double longitude = Double.parseDouble(root.get(0).get("lon").asString());
            
            log.info("[ParkingLocationDataService] Location gathered");
            return new ParkingLocationDTO(latitude, longitude);          
        }
        catch(Exception e){ 
            log.error("[ParkingLocationDataService] Failed, return null", e);
            return null;
        }        
    }
}
