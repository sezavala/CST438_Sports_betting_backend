package com.cst438.project02.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.ResponseEntity;

import com.cst438.project02.repository.BetRepository;
import com.cst438.project02.repository.UserRepository;
import com.cst438.project02.repository.GameRepository;
import com.cst438.project02.entity.Bet;
import com.cst438.project02.entity.User;
import com.cst438.project02.entity.Game;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/bet")
@CrossOrigin(origins = "https://project-02-20fa5120c543.herokuapp.com/")
public class BetController {
    @Autowired
    private BetRepository betRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    @GetMapping("/all")
    public ResponseEntity<?> getAllBets() {
        return ResponseEntity.ok(betRepository.findAll());
    }

    @PostMapping("/place")
    public ResponseEntity<?> placeBet(@RequestBody Map<String, Object> body) {
        try {
            // Extract IDs from nested objects
            @SuppressWarnings("unchecked")
            Map<String, Object> userMap = (Map<String, Object>) body.get("user");
            @SuppressWarnings("unchecked")
            Map<String, Object> gameMap = (Map<String, Object>) body.get("game");

            Long userId = ((Number) userMap.get("id")).longValue();
            Long gameId = ((Number) gameMap.get("id")).longValue();
            Double amount = ((Number) body.get("amount")).doubleValue();
            String status = (String) body.get("status");

            // Fetch entities from database
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.badRequest().body("Error: User not found with id " + userId);
            }

            Game game = gameRepository.findById(gameId).orElse(null);
            if (game == null) {
                return ResponseEntity.badRequest().body("Error: Game not found with id " + gameId);
            }

            // Create and save bet
            Bet bet = new Bet(user, game, amount, status, LocalDateTime.now());
            betRepository.save(bet);

            return ResponseEntity.ok(bet);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Error placing bet: " + e.getMessage());
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getBetsByStatus(@PathVariable String status) {
        List<Bet> bets = betRepository.findByStatus(status);
        if (bets.isEmpty()) {
            return ResponseEntity.badRequest().body("No bets found for status: " + status);
        } else {
            return ResponseEntity.ok(bets);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getBetsByUserId(@PathVariable Long userId) {
        List<Bet> bets = betRepository.findAll().stream()
                .filter(bet -> bet.getUser().getId().equals(userId))
                .toList();
        if (bets.isEmpty()) {
            return ResponseEntity.badRequest().body("No bets found for user id: " + userId);
        } else {
            return ResponseEntity.ok(bets);
        }
    }

    @GetMapping("/game/{gameId}")
    public ResponseEntity<?> getBetsByGameId(@PathVariable Long gameId) {
        List<Bet> bets = betRepository.findAll().stream()
                .filter(bet -> bet.getGame().getId().equals(gameId))
                .toList();
        if (bets.isEmpty()) {
            return ResponseEntity.badRequest().body("No bets found for game id: " + gameId);
        } else {
            return ResponseEntity.ok(bets);
        }
    }
}