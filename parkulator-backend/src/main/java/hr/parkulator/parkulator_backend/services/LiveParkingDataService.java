package hr.parkulator.parkulator_backend.services;

import org.springframework.stereotype.Service;

import org.springframework.web.reactive.function.client.WebClient;

import hr.parkulator.parkulator_backend.dto.LiveParkingDataDTO;
import hr.parkulator.parkulator_backend.dto.WorkHoursDTO;
import hr.parkulator.parkulator_backend.shared.WorkDayEnum;
import tools.jackson.databind.JsonNode;

import java.util.*;

@Service
public class LiveParkingDataService {

    //For refreshing occupancy data and checking online state of online parkings
    //public List<LiveParkingRefreshDTO> refreshRijekaPlusData() {
    //} 

    public  List<LiveParkingDataDTO> getInitialRijekaPlusData() {
        //Get json from Rijeka Plus REST API Endpoint
        WebClient webClient = WebClient.create();
        JsonNode parkiralista = webClient.get()
            .uri("https://www.rijeka-plus.hr/wp-json/restAPI/v1/parkingAPI/")
            .retrieve()
            .bodyToMono(JsonNode.class)
            .block();

        
        List<LiveParkingDataDTO> lpd_list = new ArrayList<>();

        
       
        //Building an array of LiveParkingDataDTO objects
        for(JsonNode parking : parkiralista){
            
            //Parking or zone name
            String name = parking.get("parking_name").stringValue();

            //isLive is true if parking has live occupancy data (category shows this in rijeka plus case)
            boolean isLive;
            Long spots = null;
            Long availableSpots = null;
            if(parking.get("category").stringValue().equals("Garaže i zatvorena parkirališta")){
                isLive = true;
                spots = parking.get("parking_data").get("kapacitet").asLong(0);
                availableSpots = parking.get("parking_data").get("slobodno").asLong(0);
            }
            else{
                isLive = false;
                //address = "ništa";
            }
            //Website link
            String link = parking.get("link").stringValue();

            //If parking is a zone list of addresses
            String address = parking.path("parking_data").path("parkiralista-ulice").asString("NA");
            
            //External parking id
            Long externalId = parking.get("parking_data").get("parking_id").longValue();
            
            //Work hours depending on day
            List<WorkHoursDTO> workHours = new ArrayList<>();
            WorkDayEnum workdays;
            String special = null;
            int openingHour;
            int closingHour;

            //Work hours of each parking
            for(JsonNode workhours : parking.path("parking_data").path("vrijeme_naplate")){
                if(workhours.get("dani_i_sati").stringValue().equals("Radnim danom")){
                    workdays = WorkDayEnum.WORKDAY;
                }
                if(workhours.get("dani_i_sati").stringValue().equals("Subotom")){
                    workdays = WorkDayEnum.SATHURDAY;
                }
                if(workhours.get("dani_i_sati").stringValue().equals("Radnim danom, subotom, nedjeljom i blagdanom:")){
                    workdays = WorkDayEnum.ALLDAYS;
                }
                else {
                    workdays = WorkDayEnum.SPECIAL;
                    special = workhours.get("dani_i_sati").stringValue();
                }
                String[] opening = workhours.get("vrijeme_start").stringValue().split(":");
                openingHour = Integer.parseInt(opening[0]);
                String[] closing = workhours.get("vrijeme_kraj").stringValue().split(":");;
                closingHour = Integer.parseInt(closing[0]);

                WorkHoursDTO wh = new WorkHoursDTO(workdays, special, openingHour, closingHour);
                workHours.add(wh);
            }

            
            LiveParkingDataDTO lpd = new LiveParkingDataDTO(externalId, name, 0.00, address, link, isLive, spots, availableSpots, workHours);
            lpd_list.add(lpd);
        }
            
            

            return lpd_list;
    }
}