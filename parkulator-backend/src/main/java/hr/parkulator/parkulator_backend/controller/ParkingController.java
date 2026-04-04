package hr.parkulator.parkulator_backend.controller;


import hr.parkulator.parkulator_backend.model.Parking;
import hr.parkulator.parkulator_backend.repository.ParkingRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController //vraca JSON odgovor + klasa je kontroler/API 
@RequestMapping("/parkings") //svi endpointi u ovom kontroleru pocinju sa /parkings
public class ParkingController { //kontroler za upravljanje parkiralistima

    private final ParkingRepository parkingRepository;//veza na repository=veza na bazu

//
    public ParkingController(ParkingRepository parkingRepository) {//
        this.parkingRepository = parkingRepository;
    }

    @GetMapping("/test")
public String test() {
    return "RADI";
}

    @GetMapping //endpoint za dohvacanje svih parkiralista - GET /parkings vraca listu parkiralista
    public List<Parking> getAllParkings() {
        return parkingRepository.findAll();//metoda findAll() vraca sve parkiralista iz baze kao JSON odg (vraca listu)
    }
    @PostMapping
    //POST /parkings ,kreira novi parking
public Parking createParking(@RequestBody Parking parking) {
    return parkingRepository.save(parking);//sprema novi parking u bazu
}
}