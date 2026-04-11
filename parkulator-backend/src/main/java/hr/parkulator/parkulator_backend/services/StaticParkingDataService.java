package hr.parkulator.parkulator_backend.services;

import java.io.InputStream;
import java.util.List;

import hr.parkulator.parkulator_backend.dto.ParkingRefreshDTO;
import hr.parkulator.parkulator_backend.dto.ParkingDataDTO;

import org.springframework.stereotype.Service;
import org.springframework.core.io.ClassPathResource;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Service
public class StaticParkingDataService {
    
    public List<ParkingDataDTO> getInitialStaticParkingData(){
        try{
            InputStream inputStream = new ClassPathResource("data/parkings.json").getInputStream();
            ObjectMapper om = new ObjectMapper();
            return om.readValue(inputStream, new TypeReference<List<ParkingDataDTO>>() {});
        } catch(Exception e){
            throw new RuntimeException("Can't open parking.json", e);
        }
        
    }

    public List<ParkingRefreshDTO> getRefreshStaticParkingData(){
        try{
            InputStream inputStream = new ClassPathResource("data/parkingsUpdate.json").getInputStream();
            ObjectMapper om = new ObjectMapper();
            return om.readValue(inputStream, new TypeReference<List<ParkingRefreshDTO>>() {});
        } catch(Exception e){
            throw new RuntimeException("Can't open parkingsUpdate.json", e);
        }
    }
    /*
    //Removed 
     public List<StaticParkingDataDTO> RijekaPlusScraper() {
        
        List<StaticParkingDataDTO> StaticParkingList = new ArrayList<>();
        
        try{
        Document doc = Jsoup.connect("https://www.rijeka-plus.hr/kategorija/parkiralista").get(); //connect to site, get html
        Element open_parkings_all = doc.selectFirst(".lista-otvorena"); //select part of website where static parkings are listed 
        
        Elements zones = open_parkings_all.select("article");//select each parking zone
        
        for(Element z : zones){
            //for each parking zone getting zone name, price, and a list of addreses
            String zone = z.select(".naziv").text();
            String price = z.select(".cijena").text();

            String[] addresses = z.select(".small-parkinglist").text().split("\\s*,\\s*");
            

            for(String s : addresses){
                //for each address in list of addresses clean name and create an instance of StaticParkingDataDTO
                if(s.contains("(")){
                    s = s.substring(s.indexOf('(')).trim();
                }
                if(s.endsWith(")")){
                    s = s.substring(0, s.length()-1).trim();
                }

                if(s.contains(" i ")){
                    String[] iSplit = s.split("\"\\\\s+i\\\\s+\"");
                    
                    for(String iSplitted : iSplitt){
                        if(iSplitted.isBlank()) continue;
                        StaticParkingDataDTO spd = new StaticParkingDataDTO(iSplitted, zone, price);
                        StaticParkingList.add(spd);
                    }

                    
                }
                else{
                    StaticParkingDataDTO spd = new StaticParkingDataDTO(s, zone, price);
                    StaticParkingList.add(spd);
                }
            }
        }
        return StaticParkingList;
        }
        catch(IOException e){
            return null;
        }
    }
        */
}

