package hr.parkulator.parkulator_backend.services;

import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import hr.parkulator.parkulator_backend.repositories.ParkingRepository;
import hr.parkulator.parkulator_backend.shared.WorkDayEnum;
import hr.parkulator.parkulator_backend.entities.ParkingPrice;
import hr.parkulator.parkulator_backend.exception.BadRequestException;
import hr.parkulator.parkulator_backend.exception.NoParkingsFoundException;
import hr.parkulator.parkulator_backend.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import hr.parkulator.parkulator_backend.dto.parking.ParkingDTO;
import hr.parkulator.parkulator_backend.dto.parking.ParkingSearchResponseDTO;
import hr.parkulator.parkulator_backend.entities.Parking;

@Service
@AllArgsConstructor
public class ParkingService {
    private ParkingRepository parkingRepository;

    public List<ParkingDTO> getAllParkings(){
        //Getting all parking lots and mapping to ParkingDTO for testing purposes (displaying on frontend) 
        //will be removed later as it is not necessary for this application

        List<Parking> parking_lots = parkingRepository.findAll();
        List <ParkingDTO> parkingLotsDTO = new ArrayList<>();

        for(Parking parking : parking_lots){
            ParkingDTO parkingDTO = new ParkingDTO();
            
            parkingDTO.setName(parking.getName());
            parkingDTO.setAddress(parking.getAddress());
            parkingDTO.setType(parking.getType());
            parkingDTO.setLink(parking.getLink());
            parkingDTO.setLive(parking.isLive());
            parkingDTO.setAvailableSpots(parking.getAvailableSpots());
            List<ParkingPrice> parkingPrices = parking.getParkingPrices();

            //Deciding which price to send depending on the time and date
            for(ParkingPrice parkingPrice : parkingPrices){
                DayOfWeek day = LocalDate.now().getDayOfWeek();
                int hourNow = LocalTime.now().getHour();
                
                WorkDayEnum wde = WorkDayEnum.SPECIAL;
                if(day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY){
                    wde = WorkDayEnum.WORKDAY;
                }
                else if(day == DayOfWeek.SATURDAY){
                    wde = WorkDayEnum.SATURDAY;
                }
                else if(day == DayOfWeek.SUNDAY){
                    wde = WorkDayEnum.SUNDAY;
                }
                
                if(parkingPrice.getDay() != WorkDayEnum.SPECIAL){
                    if(parkingPrice.getDay() == wde || parkingPrice.getDay() == WorkDayEnum.ALLDAYS){
                        
                        int open  = parkingPrice.getOpeningHour();
                        int close = parkingPrice.getClosingHour();  
                        boolean inRange = (open <= close  && hourNow >= open  && hourNow <= close) || (open >  close && (hourNow >= open || hourNow <= close));

                        if((open == 0 && close == 0) || inRange){
                            parkingDTO.setOpeningHour(open);
                            parkingDTO.setClosingHour(close);
                            parkingDTO.setPrice(parkingPrice.getPrice());
                        }
                    }
                }
                else{
                    //Special should display the special message, will be implemented later
                }
            }
            parkingLotsDTO.add(parkingDTO);
        }



        return parkingLotsDTO;
    }

    public Parking getParkingById(Long id) {
        return parkingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parking with id " + id + " not found"));
    }

    public Parking getParkingByName(String name) {
        return parkingRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Parking with name " + name + " not found"));
    }

    public Parking getParkingByAddress(String address) {
        return parkingRepository.findByAddress(address)
                .orElseThrow(() -> new ResourceNotFoundException("Parking with address " + address + " not found"));
    }

    public List<Parking> getParkingByType(String type) {
        return parkingRepository.findByType(type);
    }

   public ParkingSearchResponseDTO getFilteredParkings(
        String type,
        Double maxDistance,
        Double lat,
        Double lng,
        Double maxPrice
    ) {
        if (lat == null || lng == null) {
            throw new BadRequestException("User location (lat/lng) is required for filtering");
        }
        if (maxDistance != null && maxDistance < 0) {
            throw new BadRequestException("maxDistance cannot be negative");
        }
        if (maxPrice != null && maxPrice < 0) {
            throw new BadRequestException("maxPrice cannot be negative");
        }
    
        List<Parking> parkingEntities;
        boolean expanded = false;
        Double radius = null;

        //distance and type filter
        if (maxDistance == null) {
            if (type != null) {
                parkingEntities = parkingRepository.findByType(type);
            }else {
                parkingEntities = parkingRepository.findAll();
            }
        }else {
            radius = maxDistance;

            List<Parking> result = new ArrayList<>();

            while (radius <= 20) {
                result = parkingRepository.filterAll(type, radius, lat, lng);

                if (!result .isEmpty()) {
                    break;
                }

                radius += 0.5;
                expanded = true;
            }

            parkingEntities = result;
        }

        // turning enitiy to DTO
        List<ParkingDTO> parkings = parkingEntities.stream()
                .map(this::mapToDTO)
                .toList();

        // price filter on DTO 
        if (maxPrice != null) {
            List<ParkingDTO> filtered = new ArrayList<>();

            for (ParkingDTO p : parkings) {
                if (p.getPrice() >= 0 && p.getPrice() <= maxPrice) {
                    filtered.add(p);
                }
            }

            parkings = filtered;
        }

        if (parkings.isEmpty()) {
            throw new NoParkingsFoundException("No parkings found");
        }

        ParkingSearchResponseDTO response = new ParkingSearchResponseDTO();
        response.setParkings(parkings);
        response.setRadiusExpanded(expanded);
        response.setFinalRadius(radius);

        return response;
    }

    private ParkingDTO mapToDTO(Parking parking) {
        ParkingDTO dto = new ParkingDTO();

        dto.setName(parking.getName());
        dto.setAddress(parking.getAddress());
        dto.setType(parking.getType());
        dto.setLink(parking.getLink());
        dto.setLive(parking.isLive());
        dto.setAvailableSpots(parking.getAvailableSpots());

        dto.setLatitude(parking.getLatitude());
        dto.setLongitude(parking.getLongitude());

        StringBuilder message = new StringBuilder();
        double price = getCurrentPrice(parking, message);

        dto.setPrice(price);
        dto.setPriceMessage(message.length() == 0 ? null : message.toString());

        return dto;
    }

    //price calculation
    private double getCurrentPrice(Parking parking, StringBuilder message) {
        DayOfWeek day = LocalDate.now().getDayOfWeek();
        int hourNow = LocalTime.now().getHour();

        WorkDayEnum wde;

        if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) {
            wde = WorkDayEnum.WORKDAY; //mon-fri
        } else if (day == DayOfWeek.SATURDAY) {
            wde = WorkDayEnum.SATURDAY;
        } else {
            wde = WorkDayEnum.SUNDAY;
        }

         boolean hasRuleForToday = parking.getParkingPrices().stream()
            .anyMatch(r -> r.getDay() == wde || r.getDay() == WorkDayEnum.ALLDAYS);

        double price = 0.0;
        boolean found = false;

        for (ParkingPrice priceRule : parking.getParkingPrices()) {

            if (priceRule.getDay() == WorkDayEnum.SPECIAL) {
                continue;
            }

            if (priceRule.getDay() != wde &&
                priceRule.getDay() != WorkDayEnum.ALLDAYS) {
                continue;
            }

            int open = priceRule.getOpeningHour();
            int close = priceRule.getClosingHour();

            boolean inRange =
                    (open <= close && hourNow >= open && hourNow <= close)
                    || (open > close && (hourNow >= open || hourNow <= close));

            if ((open == 0 && close == 0) || inRange) {
                price = priceRule.getPrice();
                found = true;
                break;
            }
        }

        if (!hasRuleForToday) {
        message.append("No pricing information available for today. The parking may be free or closed.");
        return 0.0;
        }

        if (!found) {
            message.append("No active pricing rule for the current time");
            return 0.0;
        }

        return price;
    }
}
