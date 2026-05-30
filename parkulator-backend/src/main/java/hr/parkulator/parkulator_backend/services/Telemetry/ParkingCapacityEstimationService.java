package hr.parkulator.parkulator_backend.services.Telemetry;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.time.Instant;
import java.time.Duration;

import hr.parkulator.parkulator_backend.repositories.LocationLogRepository;
import hr.parkulator.parkulator_backend.repositories.ParkingRepository;
import jakarta.transaction.Transactional;
import hr.parkulator.parkulator_backend.entities.LocationLog;
import hr.parkulator.parkulator_backend.entities.Parking;
import hr.parkulator.parkulator_backend.shared.ParkingOccupancyCategory;

@Service
public class ParkingCapacityEstimationService {

    private static final double DEFAULT_SCORE = 0.5;
    private final ParkingRepository parkingRepository;
    private final LocationLogRepository locationLogRepository;

    public ParkingCapacityEstimationService(LocationLogRepository locationLogRepository, ParkingRepository parkingRepository){
        this.locationLogRepository = locationLogRepository;
        this.parkingRepository = parkingRepository;
    }

    @Scheduled(fixedRate = 300000) // Every 5 minutes
    @Transactional
    public void estimateParkingCapacity() {
        //Runns every 5 minutes and estimates parking capacity based on location logs from the last 5 minutes

        //Get all location logs from the last 5 minutes
        List<LocationLog> locationLogs = locationLogRepository.findByEventTimestampBetweenOrderByEventTimestampAsc(
            Instant.now().minus(Duration.ofMinutes(5)), Instant.now()
        );

        List<Parking> updatedParkings = new ArrayList<>();

        if(locationLogs.isEmpty()) return;

        //Apply each log to the corresponding parking
        for(LocationLog log : locationLogs) {
            Parking parking = log.getParking();

            if(parking == null || parking.isLive() || log.getCategory() == null || log.getParkingLikelihood() == null) continue;

            applyLogToParking(log, parking);
            updatedParkings.add(parking);

        }

        for (Parking parking : updatedParkings) {
            parkingRepository.save(parking);
        }

    }

    private void applyLogToParking(LocationLog log, Parking parking) {

        //current score from the parking entity, if null, use default score
        double currentScore = parking.getEstimatedScore() == null ? DEFAULT_SCORE : parking.getEstimatedScore();

        //Calculate the log's impact on the score, with regard to the parking likelihood and the category's impact factor
        double delta = calculateLogDelta(log);

        //Update the parking's score by applying the log's impact, and clamp it between 0 and 1
        double updatedScore = clampScore(currentScore + delta);

        parking.setEstimatedScore(updatedScore);
        parking.setOccupancyStatus(mapScoreToStatus(updatedScore));
    }

    private ParkingOccupancyCategory mapScoreToStatus(double score) {
        if (score < 0.30) {
            return ParkingOccupancyCategory.LIKELY_EMPTY;
        }

        if (score < 0.70) {
            return ParkingOccupancyCategory.MODERATELY_OCCUPIED;
        }

        return ParkingOccupancyCategory.LIKELY_FULL;
    }

    private double calculateLogDelta(LocationLog log) {
        return log.getCategory().getImpact() * log.getParkingLikelihood();
    }

    private double clampScore(double score) {
        return Math.max(0.0, Math.min(1.0, score));
    }

}
