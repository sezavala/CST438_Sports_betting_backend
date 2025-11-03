package com.cst438.project02.controller;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cst438.project02.entity.Game;
import com.cst438.project02.entity.Team;
import com.cst438.project02.repository.GameRepository;
import com.cst438.project02.repository.TeamRepository;



@RestController
@RequestMapping("/game")
@CrossOrigin("*")
public class GameController {

    @Autowired
    private final TeamRepository teamRepository;

    @Autowired
    private GameRepository gameRepository;

    public GameController(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllGames() {
        return ResponseEntity.ok(gameRepository.findAll());
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getGameById(@PathVariable Long id) {
        return gameRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping("/team/{teamId}")
    public ResponseEntity<?> getGamesByTeamName(@PathVariable String teamId) {
        List<Game> games = gameRepository.findByHomeTeamIdOrAwayTeamId(Long.valueOf(teamId), Long.valueOf(teamId));
        if (games.isEmpty()) {
            return ResponseEntity.badRequest().body("No games found for team id: " + teamId);
        } else {
            return ResponseEntity.ok(games);
        }
    }
    @GetMapping("/date/{gameDate}")
    public ResponseEntity<?> getGamesByDate(@PathVariable String gameDate) {
        LocalDate date = LocalDate.parse(gameDate);
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        List<Game> games = gameRepository.findByGameTimeBetween(startOfDay, endOfDay);
        if (games.isEmpty()) {
            return ResponseEntity.badRequest().body("No games found for date: " + gameDate);
        } else {
            return ResponseEntity.ok(games);
        }
    }
   @PostMapping("/add")
    public ResponseEntity<?> addGame(@RequestBody Map<String, Object> body) {
        try {
            Long homeTeamId = ((Number) body.get("homeTeamId")).longValue();
            Long awayTeamId = ((Number) body.get("awayTeamId")).longValue();
            String gameTimeStr = (String) body.get("gameTime");
            String result = (String) body.get("result");
            String location = (String) body.getOrDefault("location", "");

            Team homeTeam = teamRepository.findById(homeTeamId).orElse(null);
            Team awayTeam = teamRepository.findById(awayTeamId).orElse(null);

            if (homeTeam == null || awayTeam == null) {
                return ResponseEntity.badRequest().body("Invalid team IDs provided.");
            }

            LocalDateTime gameTime = LocalDateTime.parse(gameTimeStr);
            Game newGame = new Game(homeTeam, awayTeam, gameTime, result);
            
            // Set location if provided, otherwise constructor already set it to home team's city
            if (location != null && !location.isEmpty()) {
                newGame.setLocation(location);
            }
            
            // Set scores if provided
            if (body.containsKey("scoreHome")) {
                newGame.setScoreHome(((Number) body.get("scoreHome")).intValue());
            }
            if (body.containsKey("scoreAway")) {
                newGame.setScoreAway(((Number) body.get("scoreAway")).intValue());
            }
            
            gameRepository.save(newGame);

            return ResponseEntity.ok("Game added successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Error adding game: " + e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateGame(@PathVariable Long id, @RequestBody Game gameDetails) {
        return gameRepository.findById(id).map(game -> {
            game.setHomeTeam(gameDetails.getHomeTeam());
            game.setAwayTeam(gameDetails.getAwayTeam());
            game.setGameTime(gameDetails.getGameTime());
            game.setGameDate(gameDetails.getGameDate());
            game.setLocation(gameDetails.getLocation());
            game.setScoreHome(gameDetails.getScoreHome());
            game.setScoreAway(gameDetails.getScoreAway());
            game.setResult(gameDetails.getResult());
            gameRepository.save(game);
            return ResponseEntity.ok("Game updated successfully");
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteGame(@PathVariable Long id) {
        return gameRepository.findById(id).map(game -> {
            gameRepository.delete(game);
            return ResponseEntity.ok("Game deleted successfully");
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }


}
