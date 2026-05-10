package hr.parkulator.parkulator_backend.services.ParkingServices;

import java.util.List;

import org.springframework.stereotype.Service;
import hr.parkulator.parkulator_backend.dto.parking.ParkingDTO;
import hr.parkulator.parkulator_backend.exception.BadRequestException;

@Service
public class ParkingScoreService {
    public List<ParkingDTO> score(List<ParkingDTO> parkings, Double userLat, Double userLng){

        if (parkings == null || parkings.isEmpty()) {
            return parkings;
        }

        double[] prices = new double[parkings.size()];
        double[] distances = new double[parkings.size()];
        double[] availabilities = new double[parkings.size()];


        for (int i = 0; i < parkings.size(); i++) {
            ParkingDTO parking = parkings.get(i);

            double distance = calculateDistance(
                    userLat,
                    userLng,
                    parking.getLatitude(),
                    parking.getLongitude()
            );

            double availability = resolveAvailability(parking);

            prices[i] = parking.getPrice();
            distances[i] = distance;
            availabilities[i] = availability;
        }

        //dynamic normalization
        double maxPrice = max(prices);
        double maxDistance = max(distances);
        double maxAvailability = max(availabilities);

        if (maxPrice == 0) maxPrice = 1;
        if (maxDistance == 0) maxDistance = 1;
        if (maxAvailability == 0) maxAvailability = 1;

        //scoring each
        for (int i = 0; i < parkings.size(); i++) {

            ParkingDTO parking = parkings.get(i);

            String message = parking.getParkingStatus();
            double priceScore = maxPrice == 0 ? 0 : prices[i] / maxPrice;
            double distanceScore = maxDistance == 0 ? 0 : distances[i] / maxDistance;
            double availabilityScore = maxAvailability == 0 ? 0 : availabilities[i] / maxAvailability;

            double score = calculateScore(priceScore, message, distanceScore, availabilityScore);

            parking.setScore(score);
        }

        return parkings.stream()
            .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
            .toList();
    }

    private double max(double[] arr) {
        double max = 0;
        for (double value : arr) {
            if (value > max) max = value;
        }
        return max;
    }


    private double calculateScore(double price, String message, double distance, double availability) {
        double weightPrice = 0.25;
        double weightDistance = 0.45;
        double weightAvailability = 0.3;

        double priceScore = 1 + price;
        double distanceScore = 1 - distance;
        double availabilityScore = availability;
        
        double score;

        if(message != null){
            score = weightDistance * distanceScore + weightAvailability * availabilityScore;
        }else{
            score = weightPrice * priceScore + weightDistance * distanceScore + weightAvailability * availabilityScore;
        }

        //creating a better scale 0-5:
        score = score * 5.0;
        return Math.round(score * 100.0) / 100.0;
    }

    private double resolveAvailability(ParkingDTO p) {
        if (p.isLive() == true) {
            return p.getAvailableSpots();
        }

        return estimateOccupancy(p);
    }

    private double estimateOccupancy(ParkingDTO p) {
        //add algorithm for estimation of occupancy
        return 0;
    }   

    //Haversine formula
    private double calculateDistance(Double userLat, Double userLng, Double parkingLat, Double parkingLng) {
        if (userLat == null || userLng == null || parkingLat == null || parkingLng == null){
            throw new BadRequestException("Missing coordinates for distance calculation");
        }

        final double earthRadius= 6371;

        double Latitude = Math.toRadians(parkingLat - userLat);
        double Longitude = Math.toRadians(parkingLng - userLng);

        double a = Math.sin(Latitude / 2) 
                        * Math.sin(Latitude / 2)
                        + Math.cos(Math.toRadians(userLat))
                        * Math.cos(Math.toRadians(parkingLat))
                        * Math.sin(Longitude / 2)
                        * Math.sin(Longitude / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadius * c;
    }
}
