package hr.parkulator.parkulator_backend.model;

import jakarta.persistence.*;

@Entity
public class Parking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    public Long getId() {
    return id;
}

public String getName() {
    return name;
}
public void setName(String name) {
        this.name = name;
    }
}