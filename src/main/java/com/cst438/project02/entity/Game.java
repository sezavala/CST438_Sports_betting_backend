package com.cst438.project02.entity;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import java.util.List;

@Entity
@Table(name = "games")

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

    @JsonProperty("date")
    @Column(nullable = false)
    private LocalDateTime gameTime;

    @Column(nullable = false)
    private String result;

    @OneToMany(mappedBy = "game")
    private List<Bet> bets;

    public Game() {}

    public Game(Team homeTeam, Team awayTeam, LocalDateTime gameTime, String result) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.gameTime = gameTime;
        this.result = result;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Team getHomeTeam() { return homeTeam; }
    public void setHomeTeam(Team homeTeam) { this.homeTeam = homeTeam; }

    public Team getAwayTeam() { return awayTeam; }
    public void setAwayTeam(Team awayTeam) { this.awayTeam = awayTeam; }

    public LocalDateTime getGameTime() { return gameTime; }
    public void setGameTime(LocalDateTime gameTime) { this.gameTime = gameTime; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public List<Bet> getBets() { return bets; }
    public void setBets(List<Bet> bets) { this.bets = bets; }
}


