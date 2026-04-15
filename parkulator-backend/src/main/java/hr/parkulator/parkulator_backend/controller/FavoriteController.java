package hr.parkulator.parkulator_backend.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import hr.parkulator.parkulator_backend.entities.Favorite;
import hr.parkulator.parkulator_backend.services.FavoriteService;

import java.util.List;

@RestController
@RequestMapping("/favorites")
@AllArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    //  add favorite
    @PostMapping
    public Favorite addFavorite(
            @RequestParam Long userId,
            @RequestParam Long parkingId
    ) {
        return favoriteService.addFavorite(userId, parkingId);
    }

    // remove favorite
    @DeleteMapping
    public void removeFavorite(
            @RequestParam Long userId,
            @RequestParam Long parkingId
    ) {
        favoriteService.removeFavorite(userId, parkingId);
    }

    // get all favorites
    @GetMapping("/{userId}")
    public List<FavoriteDTO> getFavorites(@PathVariable Long userId) {
    return favoriteService.getUserFavorites(userId);
}
}
