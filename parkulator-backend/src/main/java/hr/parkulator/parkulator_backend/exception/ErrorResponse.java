package hr.parkulator.parkulator_backend.exception;

import java.time.LocalDateTime;

public class ErrorResponse {
    private String error;
    private String message;
    private String path;
    private LocalDateTime timestamp;

    public ErrorResponse(String error, String message, String path){
        this.error = error;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }

    public String getError(){
        return error;
    }

    public String getMessage(){
        return message;
    }

    public LocalDateTime getTimestamp(){
        return timestamp;
    }

    public String getPath(){ 
        return path; 
    }
}
