package com.cst438.project02.controller;

import com.cst438.project02.entity.favTeam;
import com.cst438.project02.repository.FavTeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/favTeam")
@CrossOrigin(origins = "https://project-02-20fa5120c543.herokuapp.com/")
public class FavController {

    @Autowired
    private FavTeamRepository favoriteTeamRepository;

    @PostMapping
    public ResponseEntity<?> addFavoriteTeam(@RequestBody FavoriteTeamRequest request) {
        try {
            Optional<favTeam> existing = favoriteTeamRepository
                    .findByUserIdAndTeamId(request.getUserId(), request.getTeamId());

            if (existing.isPresent()) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("Team is already in favorites");
            }

            favTeam favteam = new favTeam(
                    request.getUserId(),
                    request.getTeamId(),
                    request.getTeamName()
            );

            favTeam saved = favoriteTeamRepository.save(favteam);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error adding favorite team: " + e.getMessage());
        }
    }

    @DeleteMapping("/{userId}/{teamId}")
    @Transactional
    public ResponseEntity<?> removeFavoriteTeam(
            @PathVariable Long userId,
            @PathVariable Long teamId) {
        try {
            Optional<favTeam> favorite = favoriteTeamRepository
                    .findByUserIdAndTeamId(userId, teamId);

            if (favorite.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("Favorite team not found");
            }

            favoriteTeamRepository.deleteByUserIdAndTeamId(userId, teamId);
            return ResponseEntity.ok("Favorite team removed successfully");

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error removing favorite team: " + e.getMessage());
        }
    }

    @PutMapping("/{userId}/{teamId}")
    public ResponseEntity<?> updateFavoriteTeam(
            @PathVariable Long userId,
            @PathVariable Long teamId,
            @RequestBody FavoriteTeamRequest request) {
        try {
            Optional<favTeam> existing = favoriteTeamRepository.findByUserIdAndTeamId(userId, teamId);

            if (existing.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Favorite team not found");
            }

            favTeam fav = existing.get();
            fav.setTeamName(request.getTeamName()); // example of update
            favTeam updated = favoriteTeamRepository.save(fav);

            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating favorite team: " + e.getMessage());
        }
    }


    @GetMapping("/{userId}")
    public ResponseEntity<?> getFavoriteTeams(@PathVariable Long userId) {
        try {
            List<favTeam> favorites = favoriteTeamRepository.findByUserId(userId);
            return ResponseEntity.ok(favorites);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving favorite teams: " + e.getMessage());
        }
    }
}

class FavoriteTeamRequest {
    private Long userId;
    private Long teamId;
    private String teamName;

    public FavoriteTeamRequest() {}

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getTeamId() { return teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
}
