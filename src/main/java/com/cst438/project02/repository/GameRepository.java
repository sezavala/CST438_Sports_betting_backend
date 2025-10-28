package com.cst438.project02.repository;

import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.cst438.project02.entity.Game;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findByHomeTeamIdOrAwayTeamId(Long homeTeamId, Long awayTeamId);
    List<Game> findByGameTimeBetween(LocalDateTime start, LocalDateTime end);
    List<Game> findByResult(String result);
}