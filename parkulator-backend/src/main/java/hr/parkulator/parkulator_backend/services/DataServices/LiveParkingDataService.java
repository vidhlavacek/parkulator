package hr.parkulator.parkulator_backend.services.DataServices;

import org.springframework.stereotype.Service;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import hr.parkulator.parkulator_backend.dto.parking.ParkingDataDTO;
import hr.parkulator.parkulator_backend.dto.parking.ParkingPriceDTO;
import hr.parkulator.parkulator_backend.dto.parking.ParkingRefreshDTO;
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
    public List<ParkingRefreshDTO> refreshRijekaPlusData() {
        //Get json from Rijeka Plus REST API Endpoint
        JsonNode parkiralista = getRijekaPlusJSON();
        List<ParkingRefreshDTO> lpr_list = new ArrayList<>();
        if(parkiralista.isNull()) return lpr_list;

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.MM.yyyy");

        //Checks live status and updates all live parkings
        for(JsonNode parking : parkiralista){
            List<ParkingPriceDTO> pp = mapParkingPrice(parking);
            String externalID = parking.get("parking_data").get("parking_id").asString("");
            String name = parking.get("parking_name").asString("");
            String address = parking.path("parking_data").path("parkiralista-ulice").asString("");
            boolean isLive = false;
            Long availableSpots = 0L;
            //Just parkings that have live data
            if(parking.get("category").stringValue().equals("Garaže i zatvorena parkirališta")){
            
            isLive = true;
            
            //Checking live status
            if(!parking.path("parking_data").path("live_status").booleanValue()) isLive = false;    
                //For cases when live status is wrong but last update date was before today
                LocalDate date =LocalDate.parse(parking.path("parking_data").path("last_update_date").asString(), formatter); 
                if(date.isBefore(today)) isLive = false;
                //ID for connecting with databse, new available spots data
                availableSpots = parking.get("parking_data").get("slobodno").asLong(0);
            
                ParkingRefreshDTO lpr = new ParkingRefreshDTO(createSourceKey(externalID, name, address), name, isLive, availableSpots, pp);
                lpr_list.add(lpr);
            }
            else{
                List<String> addresses = addressSplitter(address);

                for(String adr : addresses){
                    lpr_list.add(new ParkingRefreshDTO(createSourceKey(externalID, name, adr), name, isLive, availableSpots, pp));
                }
            }


           
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
            String name = parking.get("parking_name").asString("");

            //Website link
            String link = parking.get("link").stringValue();
            
            //If parking is a zone list of addresses
            String address = parking.path("parking_data").path("parkiralista-ulice").asString("");
            
            //External parking id
            String externalId = parking.get("parking_data").get("parking_id").asString("");

            //Parking type
            String type = parking.get("category").stringValue();
            
            List<ParkingPriceDTO> parkingPrices = mapParkingPrice(parking);

            //isLive is true if parking has live occupancy data (category shows this in rijeka plus case)
            boolean isLive;
            Long spots = null;
            Long availableSpots = null;

            if(type.equals("Garaže i zatvorena parkirališta")){
                isLive = true;
                spots = parking.get("parking_data").get("kapacitet").asLong(0);
                availableSpots = parking.get("parking_data").get("slobodno").asLong(0);
                
                ParkingDataDTO lpd = createParkingDataDTO(createSourceKey(externalId, name, address), name, address, link, type, isLive, spots, availableSpots, parkingPrices);
                lpd_list.add(lpd);
            }
            else{
                parkingPrices.removeIf(pp -> pp.getSpecial() != null);
                //creating each parking in a zone
                isLive = false;
                List<String> addresses = addressSplitter(address);

                for(String adr : addresses){
                    lpd_list.add(createParkingDataDTO(createSourceKey(externalId, name, adr), name, adr, link, type, isLive, spots, availableSpots, parkingPrices));
                }
                
                
            }
        }
            
            return lpd_list;
    }

    public List<ParkingPriceDTO> mapParkingPrice(JsonNode parking){
        boolean defaultPriceFlag = false;
        boolean specialPriceFlag = false;
        double parkingPrice = 0.0;
        //default price if termin = ""
        //special price if termin != ""
        for(JsonNode prices : parking.path("parking_data").path("cijena")){
            String currPrice = prices.path("termin").stringValue();
            if(currPrice.equals("Parkirališno mjesto za punjenje električnih vozila") || currPrice.equals("Kamperi parkirnu naknadu plaćaju od 0-24")) continue;

            if(currPrice.equals("") || currPrice.isBlank()){
                defaultPriceFlag = true;
                parkingPrice = prices.path("cijena").doubleValue();            }
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
                if(workhours.get("dani_i_sati").stringValue().contains("Radnim danom")){
                    workdays = WorkDayEnum.WORKDAY;
                    parkingPrices.add(createParkingPrice(workdays, special, parkingPrice, workhours));
                }
                else if(workhours.get("dani_i_sati").stringValue().contains("Subotom") || workhours.get("dani_i_sati").stringValue().equals("Subotom:")){
                    workdays = WorkDayEnum.SATURDAY;
                    parkingPrices.add(createParkingPrice(workdays, special, parkingPrice, workhours));
                }
                else if(workhours.get("dani_i_sati").stringValue().contains("Nedjeljom") || workhours.get("dani_i_sati").stringValue().equals("Nedjeljom:")){
                    workdays = WorkDayEnum.SUNDAY;
                    parkingPrices.add(createParkingPrice(workdays, special, parkingPrice, workhours));
                }
                else if(workhours.get("dani_i_sati").stringValue().contains("Radnim danom, subotom, nedjeljom i blagdanom") || workhours.get("dani_i_sati").stringValue().contains("Naplata parkiranja vrši se:") || workhours.get("dani_i_sati").stringValue().contains("Naplata se vrši od")){
                    workdays = WorkDayEnum.ALLDAYS;
                    parkingPrices.add(createParkingPrice(workdays, special, parkingPrice, workhours));
                }
                else {
                    workdays = WorkDayEnum.SPECIAL;
                    special = workhours.get("dani_i_sati").stringValue();
                    parkingPrices.add(createParkingPrice(workdays, special, parkingPrice, workhours));
                }
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
                    workdays = WorkDayEnum.SATURDAY;
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
            //If here its error :(
        }
        return parkingPrices;
    }

    public ParkingPriceDTO createParkingPrice(WorkDayEnum workdays, String special, double parkingPrice, JsonNode workhours){
                String[] opening = workhours.get("vrijeme_start").stringValue().split(":");
                int openingHour = Integer.parseInt(opening[0]);
                String[] closing = workhours.get("vrijeme_kraj").stringValue().split(":");
                int closingHour = Integer.parseInt(closing[0]);
                
                ParkingPriceDTO pp = new ParkingPriceDTO(workdays, special, openingHour, closingHour, parkingPrice);
                return pp;
    }

    public ParkingDataDTO createParkingDataDTO(String sourceKey, String name, String address, String link, String type, boolean isLive, Long spots, Long availableSpots, List<ParkingPriceDTO> parkingPrice){
        return new ParkingDataDTO(sourceKey, name, address, link, type, isLive, spots, availableSpots, parkingPrice);
    }

    public String createSourceKey(String externalId, String name, String address){
        return externalId.toLowerCase().trim() + "|" + name.toLowerCase().trim() + "|" + address.toLowerCase().trim();
    }

    public List<String> addressSplitter(String address){
        List<String> adr_final = new ArrayList<>();

        String[] addresses = address.split("\\s*,\\s*");    

        for(String adr : addresses){
            //for each address in list of addresses clean name and create an instance of StaticParkingDataDTO
            if(adr.contains("(")){
                adr = adr.substring(adr.indexOf('(') + 1).trim();
            }
            if(adr.endsWith(")")){
                adr = adr.substring(0, adr.length()-1).trim();
            }
            if(adr.contains(";")){
                adr = adr.substring(0, adr.indexOf(";"));
            }
            if(adr.endsWith(".")){
                adr = adr.substring(0, adr.indexOf("."));
            }

            if(adr.contains(" i ")){
                String[] iSplit = adr.split("\\s+i\\s+");
                
                for(String iSplitted : iSplit){
                    if(iSplitted.isBlank()) continue;
                    adr_final.add(iSplitted.trim());
                }
            }
            else{
                adr = adr.trim();
                adr_final.add(adr);
            }
        }
        return adr_final;
    }
}