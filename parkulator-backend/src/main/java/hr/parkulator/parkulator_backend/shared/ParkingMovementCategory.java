package hr.parkulator.parkulator_backend.shared;

public enum ParkingMovementCategory {
    PASSING_BY(0.0),
    APPROACHING(0.05),
    SLOW_MOVING_NEAR(0.10),
    STATIONARY_NEAR(0.20),
    LEAVING_AREA(-0.15);

    private final double impact;

    ParkingMovementCategory(double impact) {
        this.impact = impact;
    }

    public double getImpact() {
        return impact;
    }
}
