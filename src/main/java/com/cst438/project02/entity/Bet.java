package com.cst438.project02.entity;
import java.math.BigDecimal;
import java.sql.Timestamp;

import jakarta.persistence.*;

@Entity
@Table(name = "bets")
public class Bet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;
    @Column(nullable = false)
    private BigDecimal amount;
    @Column(nullable = false)
    private String status;
    @Column(nullable = false)
    private Timestamp timestamp;

    public Bet() {}
    public Bet(User user, Game game, BigDecimal amount, String status, Timestamp timestamp) {
        this.user = user;
        this.game = game;
        this.amount = amount;
        this.status = status;
        this.timestamp = timestamp;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public Game getGame() {
        return game;
    }
    public void setGame(Game game) {
        this.game = game;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public Timestamp getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
    

}
