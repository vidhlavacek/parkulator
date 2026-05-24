package hr.parkulator.parkulator_backend.services.Telemetry;

import java.util.Comparator;
import java.util.List;

import hr.parkulator.parkulator_backend.dto.LocationLogDTO;
import hr.parkulator.parkulator_backend.entities.Parking;
import hr.parkulator.parkulator_backend.repositories.LocationLogRepository;
import hr.parkulator.parkulator_backend.repositories.ParkingRepository;
import hr.parkulator.parkulator_backend.shared.ParkingMovementCategory;
import hr.parkulator.parkulator_backend.entities.LocationLog;

/*
private Double latitude1;
private Double longitude1;
private Instant timestamp1;
private Double latitude2;
private Double longitude2;
private Instant timestamp2;
private Double accuracy;
*/

public class LocationLogService {

    private final ParkingRepository parkingRepository;
    private final GeoService geoService;
    private final LocationLogRepository locationLogRepository;

    public LocationLogService(ParkingRepository parkingRepository, GeoService geoService, LocationLogRepository locationLogRepository){
        this.parkingRepository = parkingRepository;
        this.geoService = geoService;
        this.locationLogRepository = locationLogRepository;
    }
    //Radius
    private static final Double NEARBY_RADIUS_METERS = 500.0;
    
    public void filterLocationData(LocationLogDTO location){
        //Validate
        if(!isRelevantLocationData(location)) return;

        //List of parking lots near user location
        List<Parking> nearbyParkingLots = parkingRepository.filterAll(null, NEARBY_RADIUS_METERS, location.getLatitude2(), location.getLongitude2());
        
        //if none are close, ignore this log
        if(nearbyParkingLots.isEmpty()) return;

        //Calculate driving speed, heading (if possible)
        Double drivingSpeed = geoService.calculateDistanceMeters(location.getLatitude1(), 
                                                location.getLongitude1(), 
                                                location.getLatitude2(), 
                                                location.getLongitude2());

        Double heading = geoService.calculateHeading(location.getLatitude1(), 
                                          location.getLongitude1(), 
                                          location.getLatitude2(), 
                                          location.getLongitude2());

        //Choose the best parking relevant for this log with regard to distance to parking and heading
        Parking bestParking = nearbyParkingLots.stream().max(Comparator.comparingDouble(parking -> 
                                                          calculateOccupiedParking(parking, location, heading)
                                                          )).orElse(null);
        
        //Parking likelyhood calculation
        Double parkingLikelihood = calculateParkingLikelihood(bestParking, location, heading, drivingSpeed);

        //Categorize with ParkingMovementCategory
        ParkingMovementCategory parkingMovementCategory = categorize(drivingSpeed, 
                                                                     geoService.calculateDistanceMeters(location.getLatitude1(), location.getLongitude1(), bestParking.getLatitude(), bestParking.getLongitude()), 
                                                                     geoService.calculateDistanceMeters(location.getLatitude2(), location.getLongitude2(), bestParking.getLatitude(), bestParking.getLongitude()), 
                                                                     parkingLikelihood);
        
        //Map and Store to database
        LocationLog locationLog = new LocationLog();
        locationLog.setParking(bestParking);
        locationLog.setParkingLikelihood(parkingLikelihood);
        locationLog.setSpeedKmh(drivingSpeed);
        locationLog.setHeadingDegrees(heading);
        locationLog.setDistanceToParkingMeters(geoService.calculateDistanceMeters(location.getLatitude2(), location.getLongitude2(), bestParking.getLatitude(), bestParking.getLongitude()));
        locationLog.setCategory(parkingMovementCategory);
        locationLog.setEventTimestamp(location.getTimestamp2());
        locationLogRepository.save(locationLog);
    }

    private Double calculateOccupiedParking(Parking parking, LocationLogDTO location, Double heading){
        Double distanceToParkingMeters = geoService.calculateDistanceMeters(
            location.getLatitude2(),
            location.getLongitude2(),
            parking.getLatitude(),
            parking.getLongitude()
        );

        Double distanceScore = Math.max(0.0, 1.0 - (distanceToParkingMeters / 500.0));

        if (heading == null) {
            return distanceScore;
        }

        Double parkingBearing = geoService.calculateHeading(
                location.getLatitude2(),
                location.getLongitude2(),
                parking.getLatitude(),
                parking.getLongitude()
        );

        Double angleDiff = calculateAngleDifference(heading, parkingBearing);
        Double headingScore = Math.max(0.0, 1.0 - (angleDiff / 180.0));

        return 0.7 * distanceScore + 0.3 * headingScore;
    }

    private Double calculateParkingLikelihood(Parking parking, LocationLogDTO location, Double userHeading, Double speedKmh) {
        Double distanceToParkingMeters = geoService.calculateDistanceMeters(
                location.getLatitude2(),
                location.getLongitude2(),
                parking.getLatitude(),
                parking.getLongitude()
        );

        Double distanceScore = Math.max(0.0, 1.0 - (distanceToParkingMeters / 80.0));

        Double speedScore;
        if (speedKmh <= 2.0) {
            speedScore = 1.0;
        } else if (speedKmh <= 8.0) {
            speedScore = 0.75;
        } else if (speedKmh <= 20.0) {
            speedScore = 0.35;
        } else {
            speedScore = 0.0;
        }

        Double headingScore = 0.5;
        if (userHeading != null) {
            Double parkingBearing = geoService.calculateHeading(
                    location.getLatitude2(),
                    location.getLongitude2(),
                    parking.getLatitude(),
                    parking.getLongitude()
            );

            Double angleDiff = calculateAngleDifference(userHeading, parkingBearing);
            headingScore = Math.max(0.0, 1.0 - (angleDiff / 180.0));
        }

        Double likelihood =
                0.45 * distanceScore +
                0.40 * speedScore +
                0.15 * headingScore;

        return Math.max(0.0, Math.min(1.0, likelihood));
    }

    private Double calculateAngleDifference(Double angle1, Double angle2) {
        Double diff = Math.abs(angle1 - angle2);
        return diff > 180.0 ? 360.0 - diff : diff;
    }

    private ParkingMovementCategory categorize(Double speedKmh, Double distance1ToParkingMeters, Double distance2ToParkingMeters, Double parkingLikelihood) {
        Double distanceDelta = distance2ToParkingMeters - distance1ToParkingMeters;

        boolean isApproaching = distanceDelta < -3.0;
        boolean isLeaving = distanceDelta > 3.0;

        if (distance2ToParkingMeters <= 10.0 && speedKmh <= 2.0 && parkingLikelihood >= 0.75) {
            return ParkingMovementCategory.STATIONARY_NEAR;
        }

        if (distance2ToParkingMeters <= 25.0 && speedKmh <= 8.0 && parkingLikelihood >= 0.55) {
            return ParkingMovementCategory.SLOW_MOVING_NEAR;
        }

        if (isLeaving && speedKmh > 3.0) {
            return ParkingMovementCategory.LEAVING_AREA;
        }

        if (isApproaching && parkingLikelihood >= 0.35) {
            return ParkingMovementCategory.APPROACHING;
        }

        return ParkingMovementCategory.PASSING_BY;
    }

    private boolean isRelevantLocationData(LocationLogDTO location){

        if(location.getTimestamp2().isBefore(location.getTimestamp1()) || location.getTimestamp1().equals(location.getTimestamp2())) return false;

        long seconds = java.time.Duration.between(location.getTimestamp1(), location.getTimestamp2()).getSeconds();
        
        if(seconds < 3 || seconds > 15) return false;

        if(location.getAccuracy() > 30.0) return false;
        
        if(!geoService.isInRijekaScope(location.getLatitude1(), location.getLongitude1()) || !geoService.isInRijekaScope(location.getLatitude2(), location.getLongitude2())) return false; 
        
        return true;
    }

   

    
}
