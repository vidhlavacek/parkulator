package hr.parkulator.parkulator_backend.services;

import org.springframework.stereotype.Service;

import org.springframework.web.reactive.function.client.WebClient;

@Service
public class LiveParkingDataService {

    public String test() {
        WebClient webClient = WebClient.create();

        String response = webClient.get()
            .uri("https://www.rijeka-plus.hr/wp-json/restAPI/v1/parkingAPI/")
            .retrieve()
            .bodyToMono(String.class)
            .block();
            
            return response;
    }
}