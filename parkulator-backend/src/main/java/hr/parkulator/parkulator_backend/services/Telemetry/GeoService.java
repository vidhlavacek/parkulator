package hr.parkulator.parkulator_backend.services.Telemetry;

import java.time.Instant;

import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class GeoService {
    //Rijeka Box
    private static final double RIJEKA_MIN_LAT = 45.28;
    private static final double RIJEKA_MAX_LAT = 45.38;
    private static final double RIJEKA_MIN_LNG = 14.38;
    private static final double RIJEKA_MAX_LNG = 14.52;

    private static final double EARTH_RADIUS_METERS = 6371000.0;

    public boolean isInRijekaScope(Double lat, Double lng){
        return lat != null && lng != null 
                && lat >= RIJEKA_MIN_LAT 
                && lat <= RIJEKA_MAX_LAT
                && lng >= RIJEKA_MIN_LNG
                && lng <= RIJEKA_MAX_LNG;
    }

    public Double calculateDistanceMeters(Double lat1, Double lng1, Double lat2, Double lng2){
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLon = Math.toRadians(lng2 - lng1);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_METERS * c;
    }

    public Double calculateHeading(Double lat1, Double lng1, Double lat2, Double lng2){
        double distanceMeters = calculateDistanceMeters(lat1, lng1, lat2, lng2);

        if (distanceMeters < 10.0) {
            return null;
        }

        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double deltaLonRad = Math.toRadians(lng2 - lng1);

        double x = Math.sin(deltaLonRad) * Math.cos(lat2Rad);
        double y = Math.cos(lat1Rad) * Math.sin(lat2Rad)
                - Math.sin(lat1Rad) * Math.cos(lat2Rad) * Math.cos(deltaLonRad);

        double bearingRad = Math.atan2(x, y);
        double bearingDeg = Math.toDegrees(bearingRad);

        return (bearingDeg + 360.0) % 360.0;
    }

    public double calculateSpeedKmh(
            double lat1, double lon1, Instant timestamp1,
            double lat2, double lon2, Instant timestamp2
    ) {
        long seconds = Duration.between(timestamp1, timestamp2).getSeconds();

        if (seconds <= 0) {
            throw new IllegalArgumentException("timestamp2 must be after timestamp1");
        }

        double distanceMeters = calculateDistanceMeters(lat1, lon1, lat2, lon2);
        return (distanceMeters / seconds) * 3.6;
    }
}
