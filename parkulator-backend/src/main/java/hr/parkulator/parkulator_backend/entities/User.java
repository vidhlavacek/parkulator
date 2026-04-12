package hr.parkulator.parkulator_backend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;
    private String username;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Favorite> favorites = new ArrayList<>();

    public void addFavorite(Favorite favorite) {
        favorites.add(favorite);
        favorite.setUser(this);
    }

    public void removeFavorite(Favorite favorite) {
        favorites.remove(favorite);
        favorite.setUser(null);
    }//
} 