package com.cst438.project02.entity;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "games")
@Getter
@Setter
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "home_team_id", nullable = false)
    private Team homeTeam;

    @ManyToOne
    @JoinColumn(name = "away_team_id", nullable = false)
    private Team awayTeam;

    @Column(name = "game_time", nullable = false)
    private LocalDateTime gameTime;

    // ADD THIS
    @Column(name = "game_date", nullable = false)
    private LocalDate gameDate;

    @Column(nullable = false)
    private String result;

    @OneToMany(mappedBy = "game")
    private List<Bet> bets;

    public Game() {}

    public Game(Team homeTeam, Team awayTeam, LocalDateTime gameTime, String result) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.gameTime = gameTime;
        this.gameDate = gameTime.toLocalDate(); // Extract date from gameTime
        this.result = result;
    }

}


