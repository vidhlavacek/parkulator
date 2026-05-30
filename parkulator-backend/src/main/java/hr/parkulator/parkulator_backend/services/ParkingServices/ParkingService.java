package hr.parkulator.parkulator_backend.services.ParkingServices;

import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;

import hr.parkulator.parkulator_backend.repositories.ParkingRepository;
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
    private final ParkingMapperService parkingMapperService;
    private final ParkingScoreService parkingScoreService;

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

   public ParkingSearchResponseDTO getFilteredParkings(String type, Double maxPrice, Double maxDistance, Double lat, Double lng){
        validateCoordinates(lat, lng);
        validateFilters(maxDistance, maxPrice);
    
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

                if (!result.isEmpty()) {
                    break;
                }

                radius = Math.round((radius * 1.5) * 100.0) / 100.0;
                expanded = true;
            }

            parkingEntities = result;
        }

        //turning entity to DTO
        List<ParkingDTO> parkings = parkingEntities.stream()
                .map(parkingMapperService::mapToDTO)
                .toList();

        //price filter on DTO 
        if (maxPrice != null) {
            parkings = parkings.stream()
                .filter(p -> p.getPrice() >= 0 && p.getPrice() <= maxPrice)
                .toList();
        }

        if (parkings.isEmpty()) {
            throw new NoParkingsFoundException("No parkings found");
        }

        parkings = parkingScoreService.score(parkings, lat, lng);

        ParkingSearchResponseDTO response = new ParkingSearchResponseDTO();
        response.setParkings(parkings);
        response.setRadiusExpanded(expanded);
        response.setFinalRadius(radius);

        return response;
    }

    private void validateCoordinates(Double lat, Double lng){
        if (lat == null || lng == null){
            throw new BadRequestException("Latitude and longitude are required");
        }

        if (lat < -90 || lat > 90) {
            throw new BadRequestException("Latitude must be between -90 and 90");
        }

        if (lng < -180 || lng > 180) {
            throw new BadRequestException("Longitude must be between -180 and 180");
        }
    }

    private void validateFilters(
            Double maxDistance,
            Double maxPrice
    ) {

        if (maxDistance != null && maxDistance < 0) {
            throw new BadRequestException(
                    "maxDistance cannot be negative"
            );
        }

        if (maxPrice != null && maxPrice < 0) {
            throw new BadRequestException(
                    "maxPrice cannot be negative"
            );
        }
    }
}

