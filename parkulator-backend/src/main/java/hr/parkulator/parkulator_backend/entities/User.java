package hr.parkulator.parkulator_backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotBlank
    @Email
    private String email;

    @JsonIgnore
    @ToString.Exclude
    @Column(nullable = false)
    private String password;
    
    @Column(unique = true, nullable = false)
    @NotBlank
    private String username;

    @Column(updatable = false)
    private LocalDateTime dateJoined;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Favorite> favorites = new ArrayList<>();

    @PrePersist
    public void onCreate() {
        this.dateJoined = LocalDateTime.now();
    }

    public void addFavorite(Favorite favorite) {
        favorites.add(favorite);
        favorite.setUser(this);
    }

    public void removeFavorite(Favorite favorite) {
        favorites.remove(favorite);
        favorite.setUser(null);
    }
}