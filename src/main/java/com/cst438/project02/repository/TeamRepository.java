package com.cst438.project02.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.cst438.project02.entity.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
   Team findByName(String name);
   boolean existsByName(String name);
}