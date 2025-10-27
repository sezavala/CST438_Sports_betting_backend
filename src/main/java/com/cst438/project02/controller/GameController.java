package com.cst438.project02.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.cst438.project02.repository.TeamRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.cst438.project02.entity.Game;
import com.cst438.project02.entity.Team;
import com.cst438.project02.repository.GameRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import org.springframework.web.bind.annotation.CrossOrigin;



@RestController
@RequestMapping("/game")
@CrossOrigin(origins = "https://project-02-20fa5120c543.herokuapp.com/")
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
    @GetMapping("/id")
    public ResponseEntity<?> getGameById(@PathVariable Long id) {
        return gameRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping("/team/{teamName}")
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
        Long homeTeamId = Long.valueOf((Integer) body.get("homeTeamId"));
        Long awayTeamId = Long.valueOf((Integer) body.get("awayTeamId"));
        String gameTimeStr = (String) body.get("gameTime");
        String result = (String) body.get("result");
        Team homeTeam = teamRepository.findById(homeTeamId).orElse(null);
        Team awayTeam = teamRepository.findById(awayTeamId).orElse(null);
        if (homeTeam == null || awayTeam == null) {
            return ResponseEntity.badRequest().body("Invalid team IDs provided.");
        }
        LocalDateTime gameTime = LocalDateTime.parse(gameTimeStr);
        Game newGame = new Game(homeTeam, awayTeam, gameTime, result);
        gameRepository.save(newGame);
        return ResponseEntity.ok("Game added successfully");
        
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateGame(@PathVariable Long id, @RequestBody Game gameDetails) {
        return gameRepository.findById(id).map(game -> {
            game.sethomeTeam(gameDetails.gethomeTeam());
            game.setawayTeam(gameDetails.getawayTeam());
            game.setgameTime(gameDetails.getgameTime());
            game.setresult(gameDetails.getresult());
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
