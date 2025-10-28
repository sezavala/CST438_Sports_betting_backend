package com.cst438.project02.controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.ResponseEntity;

import com.cst438.project02.repository.BetRepository;
import com.cst438.project02.entity.Bet;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/bet")
public class BetController {
    @Autowired
    private BetRepository betRepository;

    @GetMapping("/all")
    public ResponseEntity<?> getAllBets() {
        return ResponseEntity.ok(betRepository.findAll());
    }
    @PostMapping("/place")
    public ResponseEntity<?> placeBet(@RequestBody Bet bet) {
        betRepository.save(bet);
        return ResponseEntity.ok("Bet placed successfully!");
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
    @GetMapping ("/user/{userId}")
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
    @GetMapping ("/game/{gameId}")
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
