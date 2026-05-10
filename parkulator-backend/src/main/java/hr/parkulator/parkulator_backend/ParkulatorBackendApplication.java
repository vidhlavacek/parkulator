package hr.parkulator.parkulator_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {
        "hr.parkulator.parkulator_backend"
})
@EnableScheduling
public class ParkulatorBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParkulatorBackendApplication.class, args);
    }
}