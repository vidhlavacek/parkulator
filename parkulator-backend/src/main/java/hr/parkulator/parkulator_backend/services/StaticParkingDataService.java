package hr.parkulator.parkulator_backend.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import hr.parkulator.parkulator_backend.dto.StaticParkingDataDTO;

import org.springframework.stereotype.Service;

@Service
public class StaticParkingDataService {
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
                    String[] iSplitt = s.split("\"\\\\s+i\\\\s+\"");
                    
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

        //List<String> AllAddresses = new ArrayList<>();

            
            /* 
            String address_list = address.text(); //address_list is a string with html element contents(without tags)

           // address_list.split("\\s*,\\s*"); //split on each ,
            String[] splitted = address_list.split("\\s*,\\s*");
            
            Collections.addAll(AllAddresses, splitted);
            
            //System.out.print(address.text() + "\n\n");
        }
        for(String s : AllAddresses){
            System.out.print(s+"\n");
            }*/

        //System.out.print(adress.toString());
        return StaticParkingList;
        }
        catch(IOException e){
            return null;
        }
    }
}

