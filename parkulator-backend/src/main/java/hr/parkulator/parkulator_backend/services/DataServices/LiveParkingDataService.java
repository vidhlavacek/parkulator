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
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
@Service
public class LiveParkingDataService {
    
    public JsonNode getRijekaPlusJSON(){
        //Method for connecting to Rijeka Plus REST API, returns a JSON response

        WebClient webClient = WebClient.create();
        try {
        log.info("Fetching https://www.rijeka-plus.hr/wp-json/restAPI/v1/parkingAPI/");
        JsonNode JSON = webClient.get()
            .uri("https://www.rijeka-plus.hr/wp-json/restAPI/v1/parkingAPI/")
            .retrieve()
            .bodyToMono(JsonNode.class)
            .block();

        log.info("Fetching success");
        return JSON;
        } 
        //Returns null, handled when calling the method
        catch (WebClientResponseException e) {
            log.warn("Fetching failed, https://www.rijeka-plus.hr/wp-json/restAPI/v1/parkingAPI/ not responding");
            return null;
        } 
        catch (WebClientRequestException e) {
            log.warn("Fetching failed, request failed");
            return null;
        } 
        catch (Exception e) {
            log.warn("Fetching failed, exception {}", e);
            return null;
        }
    }
    
    public List<ParkingRefreshDTO> refreshRijekaPlusData() {

        log.info("[LIVE PARKING LOT REFRESH] Refreshing parking lot data...");

        //Get json from Rijeka Plus REST API Endpoint
        JsonNode parkiralista = getRijekaPlusJSON();
        List<ParkingRefreshDTO> lpr_list = new ArrayList<>();
        if(parkiralista.isNull()) {
            log.warn("[LIVE PARKING LOT REFRESH] Error with fetch, returning null");
            return lpr_list;
        } 

        //For checking if the data is recent
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
            
            if(pp == null){
                //To be replaced with logger when logger is implemented
                System.out.print("Parking with name " + name + " and ExternalID " + externalID + "error in getting parking prices (refresh)");
                continue;
            } 
            
            //Just parkings that have live data
            if(parking.get("category").stringValue().equals("Garaže i zatvorena parkirališta")){
            
                isLive = true;
            
                //Checking live status
                if(!parking.path("parking_data").path("live_status").booleanValue()) isLive = false;    
                //For cases when live status is wrong but last update date was before today
                LocalDate date =LocalDate.parse(parking.path("parking_data").path("last_update_date").asString(), formatter); 
                if(date.isBefore(today)) isLive = false;
                //new available spots data
                availableSpots = parking.get("parking_data").get("slobodno").asLong(0);
                
                //Creating an instance of ParkingRefreshDTO, source key is created here
                ParkingRefreshDTO lpr = new ParkingRefreshDTO(createSourceKey(externalID, name, address), name, isLive, availableSpots, pp);
                lpr_list.add(lpr);
            }
            //Zone parking lots
            else{
                //Splitting the zone parking lots into streets with their addresses
                List<String> addresses = addressSplitter(address);

                for(String adr : addresses){
                    lpr_list.add(new ParkingRefreshDTO(createSourceKey(externalID, name, adr), name, isLive, availableSpots, pp));
                }
            }
        }
        log.info("[LIVE PARKING LOT REFRESH] Success");
        return lpr_list;
    } 
    
    public  List<ParkingDataDTO> getInitialRijekaPlusData() {
        log.info("[LIVE PARKING LOT INITIALIZATION] Starting...");
        //Get json from Rijeka Plus REST API Endpoint
        JsonNode parkiralista = getRijekaPlusJSON();
        List<ParkingDataDTO> lpd_list = new ArrayList<>();
        if(parkiralista.isNull()){
            log.warn("[LIVE PARKING LOT INITIALIZATION] Error with fetch, returning null");
            return lpd_list;
        }
       
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
            //Error
            if(parkingPrices == null){
                //To be replaced with logger when logger is implemented
                log.error("Parking with name " + name + " and ExternalID " + externalId + "error in getting parking prices");
                continue;
            } 

            //isLive is true if parking has live occupancy data (category shows this in rijeka plus case)
            boolean isLive;
            Long spots = null;
            Long availableSpots = null;

            //Parking that are live
            if(type.equals("Garaže i zatvorena parkirališta")){
                isLive = true;
                spots = parking.get("parking_data").get("kapacitet").asLong(0);
                availableSpots = parking.get("parking_data").get("slobodno").asLong(0);
                
                Double latitude = parking.get("parking_data").get("lokacija").get("lat").asDouble();
                Double longitude = parking.get("parking_data").get("lokacija").get("lng").asDouble();

                lpd_list.add(ParkingDataDTO.builder()
                                    .sourceKey(createSourceKey(externalId, name, address))
                                    .name(name)
                                    .address(address)
                                    .link(link)
                                    .type(type)
                                    .isLive(isLive)
                                    .spots(spots)
                                    .availableSpots(availableSpots)
                                    .latitude(latitude)
                                    .longitude(longitude)
                                    .parkingPrice(parkingPrices)
                                    .build());
            }
            //Offline parkings, these are zones which need to be split into seperate parking lots
            else{
                parkingPrices.removeIf(pp -> pp.getSpecial() != null);
                //creating each parking in a zone
                isLive = false;
                List<String> addresses = addressSplitter(address);

                for(String adr : addresses){
                    lpd_list.add(
                        ParkingDataDTO.builder()
                        .sourceKey(createSourceKey(externalId, name, address))
                        .name(name)
                        .address(address)
                        .link(link)
                        .type(type)
                        .isLive(isLive)
                        .spots(spots)
                        .availableSpots(availableSpots)
                        .latitude(null)
                        .longitude(null)
                        .parkingPrice(parkingPrices)
                        .build()
                    );
                }
            }
        }
            log.info("[LIVE PARKING LOT INITIALIZATION] Success");
            return lpd_list;
    }

    public List<ParkingPriceDTO> mapParkingPrice(JsonNode parking){
        //for reading parking prices from Rijeka Plus

        //Flags
        boolean defaultPriceFlag = false;
        boolean specialPriceFlag = false;

        //Price
        double parkingPrice = 0.0;

        //default price flag if termin = ""
        //special price flag if termin != ""
        for(JsonNode prices : parking.path("parking_data").path("cijena")){
            String currPrice = prices.path("termin").stringValue();
            
            //Information currently not important for this application
            if(currPrice.equals("Parkirališno mjesto za punjenje električnih vozila") || currPrice.equals("Kamperi parkirnu naknadu plaćaju od 0-24")) continue;
            
            if(currPrice.equals("") || currPrice.isBlank()){
                defaultPriceFlag = true;
                parkingPrice = prices.path("cijena").doubleValue();            }
            else {
                specialPriceFlag = true;
            }
        }

        //Creating a list of ParkingPriceDTO which includes pricing and work hours
        List<ParkingPriceDTO> parkingPrices = new ArrayList<>();
        WorkDayEnum workdays; //our enumeration with workdays according to the needs of the application
        String special = null;
        int openingHour;
        int closingHour;

        //different cases according to the default and special flag
        if(defaultPriceFlag && !specialPriceFlag) {
            //Default price flag, creating a ParkingPriceDTO for each mention of a different work day in our WorkDayEnum enumeration
            for(JsonNode workhours : parking.path("parking_data").path("vrijeme_naplate")){
                boolean special_flag = true;
                
                if(workhours.get("dani_i_sati").stringValue().toLowerCase().contains("radnim danom")){
                    workdays = WorkDayEnum.WORKDAY;
                    parkingPrices.add(createParkingPrice(workdays, special, parkingPrice, workhours));
                    special_flag = false;
                }
                if(workhours.get("dani_i_sati").stringValue().toLowerCase().contains("subotom") || workhours.get("dani_i_sati").stringValue().toLowerCase().equals("subotom:")){
                    workdays = WorkDayEnum.SATURDAY;
                    parkingPrices.add(createParkingPrice(workdays, special, parkingPrice, workhours));
                    special_flag = false;
                }
                if(workhours.get("dani_i_sati").stringValue().toLowerCase().contains("nedjeljom") || workhours.get("dani_i_sati").stringValue().toLowerCase().equals("nedjeljom:")){
                    workdays = WorkDayEnum.SUNDAY;
                    parkingPrices.add(createParkingPrice(workdays, special, parkingPrice, workhours));
                    special_flag = false;
                }
                if(workhours.get("dani_i_sati").stringValue().toLowerCase().contains("naplata parkiranja vrši se:") || workhours.get("dani_i_sati").stringValue().toLowerCase().contains("naplata se vrši od")){
                    workdays = WorkDayEnum.ALLDAYS;
                    parkingPrices.add(createParkingPrice(workdays, special, parkingPrice, workhours));
                    special_flag = false;
                }
                if (special_flag) {
                    workdays = WorkDayEnum.SPECIAL;
                    special = workhours.get("dani_i_sati").stringValue();
                    parkingPrices.add(createParkingPrice(workdays, special, parkingPrice, workhours));
                }
            }
        }
        else if (specialPriceFlag && !defaultPriceFlag) {
            //Special price flag where the price is written in another part of the JSON response
            for(JsonNode prices : parking.path("parking_data").path("cijena")){
                //Ignoring the data not relevant for this application
                if(prices.path("termin").stringValue().equals("Kamperi parkirnu naknadu plaćaju od 0-24") || prices.path("termin").stringValue().equals("Parkirališno mjesto za punjenje električnih vozila")) continue;
                
                //Using regular expression to get workhours from the 12-13 format or similar
                Matcher matcher = Pattern.compile("(\\d{1,2}).*?(\\d{1,2})").matcher(prices.path("termin").stringValue());
                
                //For avoidng "may not have been initialized" warning
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
            //This kinds of parking lots have their prices added by our manually written static data
        }
        else {
            //If we end up here its an error handled when calling the function
            return null;
        }
        return parkingPrices;
    }

    public ParkingPriceDTO createParkingPrice(WorkDayEnum workdays, String special, double parkingPrice, JsonNode workhours){
                //Creating a ParkingPriceDTO instance

                String[] opening = workhours.get("vrijeme_start").stringValue().split(":");
                int openingHour = Integer.parseInt(opening[0]);
                String[] closing = workhours.get("vrijeme_kraj").stringValue().split(":");
                int closingHour = Integer.parseInt(closing[0]);
                
                ParkingPriceDTO pp = new ParkingPriceDTO(workdays, special, openingHour, closingHour, parkingPrice);
                return pp;
    }

    public String createSourceKey(String externalId, String name, String address){
        //Creates a source key as a combination of external id, parking name and address
        return externalId.toLowerCase().trim() + "|" + name.toLowerCase().trim() + "|" + address.toLowerCase().trim();
    }

    public List<String> addressSplitter(String address){
        //The only way to create a Parking lot form parking zones is by creating a parking for each address
        //Parking Zones have a list of addresess. This method returnes a List of all these addresses seperated
        List<String> adr_final = new ArrayList<>();

        String[] addresses = address.split("\\s*,\\s*");    

        for(String adr : addresses){
            //for each address in list of addresses clean address
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