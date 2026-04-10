package hr.parkulator.parkulator_backend.services;

import org.springframework.stereotype.Service;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import hr.parkulator.parkulator_backend.dto.ParkingDataDTO;
import hr.parkulator.parkulator_backend.dto.LiveParkingRefreshDTO;
import hr.parkulator.parkulator_backend.dto.ParkingPriceDTO;
import hr.parkulator.parkulator_backend.shared.WorkDayEnum;
import tools.jackson.databind.JsonNode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public  List<ParkingDataDTO> getInitialRijekaPlusData() {

        //Get json from Rijeka Plus REST API Endpoint
        JsonNode parkiralista = getRijekaPlusJSON();
        List<ParkingDataDTO> lpd_list = new ArrayList<>();
        if(parkiralista.isNull()) return lpd_list;
       
        //Building an array of ParkingDataDTO objects
        for(JsonNode parking : parkiralista){
            
            //Parking or zone name
            String name = parking.get("parking_name").stringValue();

            //Website link
            String link = parking.get("link").stringValue();
            
            //If parking is a zone list of addresses
            String address = parking.path("parking_data").path("parkiralista-ulice").asString("NA");
            
            //External parking id
            Long externalId = parking.get("parking_data").get("parking_id").longValue();

            //Rijeka plus data is problematic so this part is deciding which JSON field to look at for price and workhours
            boolean defaultPriceFlag = false;
            boolean specialPriceFlag = false;
            double parkingPrice = 0.0;

            //default price if termin = ""
            //special price if termin != ""
            for(JsonNode prices : parking.path("parking_data").path("cijena")){
                String currPrice = prices.path("termin").stringValue();
                
                if(currPrice.equals("Parkirališno mjesto za punjenje električnih vozila") || currPrice.equals("Kamperi parkirnu naknadu plaćaju od 0-24")) continue;
                
                if(currPrice.equals("")){
                    defaultPriceFlag = true;
                    parkingPrice = prices.path("cijena").doubleValue();
                }
                else {
                    specialPriceFlag = true;
                }
            }

            //Creating a list of workhours and prices 
            List<ParkingPriceDTO> parkingPrices = new ArrayList<>();
            WorkDayEnum workdays;
            String special = null;
            int openingHour;
            int closingHour;


            if(defaultPriceFlag && !specialPriceFlag) {
                for(JsonNode workhours : parking.path("parking_data").path("vrijeme_naplate")){
                    if(workhours.get("dani_i_sati").stringValue().equals("Radnim danom")){
                        workdays = WorkDayEnum.WORKDAY;
                    }
                    else if(workhours.get("dani_i_sati").stringValue().equals("Subotom")){
                        workdays = WorkDayEnum.SATHURDAY;
                    }
                    else if(workhours.get("dani_i_sati").stringValue().contains("Radnim danom, subotom, nedjeljom i blagdanom")){
                        workdays = WorkDayEnum.ALLDAYS;
                    }
                    else {
                        workdays = WorkDayEnum.SPECIAL;
                        special = workhours.get("dani_i_sati").stringValue();
                    }
                    String[] opening = workhours.get("vrijeme_start").stringValue().split(":");
                    openingHour = Integer.parseInt(opening[0]);
                    String[] closing = workhours.get("vrijeme_kraj").stringValue().split(":");
                    closingHour = Integer.parseInt(closing[0]);

                    ParkingPriceDTO pp = new ParkingPriceDTO(workdays, special, openingHour, closingHour, parkingPrice);
                    parkingPrices.add(pp);
                }
            }
            else if (specialPriceFlag && !defaultPriceFlag) {
                for(JsonNode prices : parking.path("parking_data").path("cijena")){
                    if(prices.path("termin").stringValue().equals("Kamperi parkirnu naknadu plaćaju od 0-24") || prices.path("termin").stringValue().equals("Parkirališno mjesto za punjenje električnih vozila")) continue;
                    
                    Matcher matcher = Pattern.compile("(\\d{1,2}).*?(\\d{1,2})").matcher(prices.path("termin").stringValue());

                    openingHour = 5;
                    closingHour = 5;

                    if(matcher.find()){
                        openingHour = Integer.parseInt(matcher.group(1));
                        closingHour = Integer.parseInt(matcher.group(2));
                    }

                    parkingPrice = prices.path("cijena").doubleValue();
                    
                    if(prices.path("termin").stringValue().contains("Radnim danom")){
                        workdays = WorkDayEnum.WORKDAY;
                        ParkingPriceDTO pp = new ParkingPriceDTO(workdays, special, openingHour, closingHour, parkingPrice);
                        parkingPrices.add(pp);
                    }
                    if(prices.path("termin").stringValue().contains("subotom")){
                        workdays = WorkDayEnum.SATHURDAY;
                        ParkingPriceDTO pp = new ParkingPriceDTO(workdays, special, openingHour, closingHour, parkingPrice);
                        parkingPrices.add(pp);
                    }
                    if(prices.path("termin").stringValue().contains("nedjeljom")){
                        workdays = WorkDayEnum.SUNDAY;
                        ParkingPriceDTO pp = new ParkingPriceDTO(workdays, special, openingHour, closingHour, parkingPrice);
                        parkingPrices.add(pp);
                    }
                }
            } 
            else if(specialPriceFlag && defaultPriceFlag) {
                //Solved by manual data
            }
            else {
                //If here its error :()
            }


            //isLive is true if parking has live occupancy data (category shows this in rijeka plus case)
            boolean isLive;
            Long spots = null;
            Long availableSpots = null;

            if(parking.get("category").stringValue().equals("Garaže i zatvorena parkirališta")){
                isLive = true;
                spots = parking.get("parking_data").get("kapacitet").asLong(0);
                availableSpots = parking.get("parking_data").get("slobodno").asLong(0);
            
                ParkingDataDTO lpd = new ParkingDataDTO(externalId, name, address, link, isLive, spots, availableSpots, parkingPrices);
                lpd_list.add(lpd);
            }
            else{
                parkingPrices.removeIf(pp -> pp.getSpecial() != null);
                //creating each parking in a zone
                isLive = false;
                
                String[] addresses = address.split("\\s*,\\s*");
            

                for(String adr : addresses){
                    //for each address in list of addresses clean name and create an instance of StaticParkingDataDTO
                    if(adr.contains("(")){
                        adr = adr.substring(adr.indexOf('(')).trim();
                    }
                    if(adr.endsWith(")")){
                        adr = adr.substring(0, adr.length()-1).trim();
                    }

                    if(adr.contains(" i ")){
                        String[] iSplit = adr.split("\\s+i\\s+");
                        
                        for(String iSplitted : iSplit){
                            if(iSplitted.isBlank()) continue;
                            ParkingDataDTO lpd = new ParkingDataDTO(externalId, name, iSplitted, link, isLive, spots, availableSpots, parkingPrices);
                            lpd_list.add(lpd);
                        }
                    }
                    else{
                        ParkingDataDTO lpd = new ParkingDataDTO(externalId, name, adr, link, isLive, spots, availableSpots, parkingPrices);
                        lpd_list.add(lpd);
                    }
                }
            }
        }
            
            return lpd_list;
    }
}