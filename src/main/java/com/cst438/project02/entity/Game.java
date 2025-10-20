package com.cst438.project02.entity;
import java.time.LocalDateTime;
 
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "games")

public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private LocalDateTime gameDate;

    @Column(nullable = false)
    private int scoreHome;
    @Column(nullable = false)
    private int scoreAway;

    public Game() {}

    public Game(String location, LocalDateTime gameDate, int scoreHome, int scoreAway) {
        this.location = location;
        this.gameDate = gameDate;
        this.scoreHome = scoreHome;
        this.scoreAway = scoreAway;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    } 

    public LocalDateTime getGameDate() {
        return gameDate;
    }

    public void setGameDate(LocalDateTime gameDate) {
        this.gameDate = gameDate;
    }

    public int getScoreHome() {
        return scoreHome;
    }
    public void setScoreHome(int scoreHome) {
        this.scoreHome = scoreHome;
    }
    public int getScoreAway() {
        return scoreAway;
    }
    public void setScoreAway(int scoreAway) {
        this.scoreAway = scoreAway;
    }
}



