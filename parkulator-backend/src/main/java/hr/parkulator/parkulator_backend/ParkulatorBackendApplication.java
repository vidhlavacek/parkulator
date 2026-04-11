package hr.parkulator.parkulator_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "hr.parkulator.parkulator_backend"
})
public class ParkulatorBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParkulatorBackendApplication.class, args);
    }
}