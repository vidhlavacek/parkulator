package hr.parkulator.parkulator_backend.services;

import org.springframework.stereotype.Service;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import hr.parkulator.parkulator_backend.dto.LiveParkingDataDTO;
import hr.parkulator.parkulator_backend.dto.LiveParkingRefreshDTO;
import hr.parkulator.parkulator_backend.dto.WorkHoursDTO;
import hr.parkulator.parkulator_backend.shared.WorkDayEnum;

import tools.jackson.databind.JsonNode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.*;

@Service
public class LiveParkingDataService {

    //Method for connecting to Rijeka Plus REST API
    public JsonNode getRijekaPlusJSON(){
        WebClient webClient = WebClient.create();
        try {
        JsonNode JSON = webClient.get()
            .uri("https://www.rijeka-plus.hr/wp-json/restAPI/v1/parkingAPI/")
            .retrieve()
            .bodyToMono(JsonNode.class)
            .block();

        return JSON;
        } 
        catch (WebClientResponseException e) {
            return null;
        } 
        catch (WebClientRequestException e) {
            return null;
        } 
        catch (Exception e) {
            return null;
        }
    }
    //For refreshing occupancy data and checking online state of online parkings (Rijeka Plus)
    public List<LiveParkingRefreshDTO> refreshRijekaPlusData() {
        //Get json from Rijeka Plus REST API Endpoint
        JsonNode parkiralista = getRijekaPlusJSON();
        List<LiveParkingRefreshDTO> lpr_list = new ArrayList<>();
        if(parkiralista.isNull()) return lpr_list;

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.MM.yyyy");

        //Checks live status and updates all live parkings
        for(JsonNode parking : parkiralista){
            //Just parkings that have live data
            if(!parking.get("category").stringValue().equals("Garaže i zatvorena parkirališta")) continue;
            
            boolean isLive = true;
            
            //Checking live status
            if(!parking.path("parking_data").path("live_status").booleanValue()) isLive = false;
            
            //For cases when live status is wrong but last update date was before today
            LocalDate date =LocalDate.parse(parking.path("parking_data").path("last_update_date").asString(), formatter); 
            if(date.isBefore(today)) isLive = false;

            //ID for connecting with databse, new available spots data
            Long externalID = parking.get("parking_data").get("parking_id").longValue();    
            Long availableSpots = parking.get("parking_data").get("slobodno").asLong(0);
            LiveParkingRefreshDTO lpr = new LiveParkingRefreshDTO(externalID, null, isLive, availableSpots);
            lpr_list.add(lpr);
           
        }

        return lpr_list;
    } 
    //For getting initial data from Rijeka Plus 
    public  List<LiveParkingDataDTO> getInitialRijekaPlusData() {

        //Get json from Rijeka Plus REST API Endpoint
        JsonNode parkiralista = getRijekaPlusJSON();
        List<LiveParkingDataDTO> lpd_list = new ArrayList<>();
        if(parkiralista.isNull()) return lpd_list;
       
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
                else if(workhours.get("dani_i_sati").stringValue().equals("Subotom")){
                    workdays = WorkDayEnum.SATHURDAY;
                }
                else if(workhours.get("dani_i_sati").stringValue().equals("Radnim danom, subotom, nedjeljom i blagdanom:")){
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