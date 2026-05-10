package hr.parkulator.parkulator_backend.exception;

public class NoParkingsFoundException extends RuntimeException{
    public NoParkingsFoundException(String message) {
        super(message);
    }
}