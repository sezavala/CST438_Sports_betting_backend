package com.cst438.project02.controller;

import com.cst438.project02.entity.Game;
import com.cst438.project02.entity.Team;
import com.cst438.project02.repository.GameRepository;
import com.cst438.project02.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/game")
@CrossOrigin(origins = {"https://project-02-20fa5120c543.herokuapp.com", "http://localhost:8080"})
public class GameController {

    private final TeamRepository teamRepository;
    private final GameRepository gameRepository;

    @Autowired
    public GameController(TeamRepository teamRepository, GameRepository gameRepository) {
        this.teamRepository = teamRepository;
        this.gameRepository = gameRepository;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Game>> getAllGames() {
        return ResponseEntity.ok(gameRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getGameById(@PathVariable Long id) {
        return gameRepository.findById(id)
                .<ResponseEntity<Object>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Game not found with id: " + id));
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<?> getGamesByTeamId(@PathVariable Long teamId) {
        List<Game> games = gameRepository.findByHomeTeamIdOrAwayTeamId(teamId, teamId);
        if (games.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No games found for team ID: " + teamId);
        }
        return ResponseEntity.ok(games);
    }

    @GetMapping("/date/{gameDate}")
    public ResponseEntity<?> getGamesByDate(@PathVariable String gameDate) {
        try {
            LocalDate date = LocalDate.parse(gameDate);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
            List<Game> games = gameRepository.findByGameTimeBetween(startOfDay, endOfDay);

            if (games.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No games found for date: " + gameDate);
            }
            return ResponseEntity.ok(games);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid date format. Use YYYY-MM-DD");
        }
    }

    @GetMapping("/date-range/{startDate}/{endDate}/{teamId}")
    public ResponseEntity<?> getGamesByTeamAndDateRange(
            @PathVariable String startDate,
            @PathVariable String endDate,
            @PathVariable Long teamId) {

        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            LocalDateTime startOfRange = start.atStartOfDay();
            LocalDateTime endOfRange = end.atTime(LocalTime.MAX);

            List<Game> teamGames = gameRepository.findByHomeTeamIdOrAwayTeamId(teamId, teamId);
            List<Game> filtered = teamGames.stream()
                    .filter(g -> !g.getGameTime().isBefore(startOfRange) && !g.getGameTime().isAfter(endOfRange))
                    .toList();

            if (filtered.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No games found for team " + teamId + " between " + startDate + " and " + endDate);
            }
            return ResponseEntity.ok(filtered);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error parsing dates or retrieving games: " + e.getMessage());
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addGame(@RequestBody Map<String, Object> body) {
        try {
            Long homeTeamId = Long.valueOf((Integer) body.get("homeTeamId"));
            Long awayTeamId = Long.valueOf((Integer) body.get("awayTeamId"));
            String gameTimeStr = (String) body.get("gameTime");
            String result = (String) body.get("result");

            Team homeTeam = teamRepository.findById(homeTeamId).orElse(null);
            Team awayTeam = teamRepository.findById(awayTeamId).orElse(null);

            if (homeTeam == null || awayTeam == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid team IDs provided.");
            }

            LocalDateTime gameTime = LocalDateTime.parse(gameTimeStr);
            Game newGame = new Game(homeTeam, awayTeam, gameTime, result);
            gameRepository.save(newGame);
            return ResponseEntity.status(HttpStatus.CREATED).body(newGame);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error adding game: " + e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateGame(@PathVariable Long id, @RequestBody Game gameDetails) {
        return gameRepository.findById(id).map(game -> {
            game.setHomeTeam(gameDetails.getHomeTeam());
            game.setAwayTeam(gameDetails.getAwayTeam());
            game.setGameTime(gameDetails.getGameTime());
            game.setResult(gameDetails.getResult());
            gameRepository.save(game);
            return ResponseEntity.ok("Game updated successfully");
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Game not found with id: " + id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteGame(@PathVariable Long id) {
        return gameRepository.findById(id).map(game -> {
            gameRepository.delete(game);
            return ResponseEntity.ok("Game deleted successfully");
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Game not found with id: " + id));
    }
}
