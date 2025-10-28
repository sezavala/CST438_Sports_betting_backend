package com.cst438.project02.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.cst438.project02.entity.Bet;
import com.cst438.project02.entity.User;
import com.cst438.project02.entity.Game;
import java.util.List;

@Repository
public interface BetRepository extends JpaRepository<Bet, Long> {
    List<Bet> findByUser(User user);
    List<Bet> findByGame(Game game);
    List<Bet> findByStatus(String status);

}
