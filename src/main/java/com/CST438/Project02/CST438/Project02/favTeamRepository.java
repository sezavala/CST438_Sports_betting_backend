package com.CST438.Project02.CST438.Project02;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface favTeamRepository extends JpaRepository<favTeam, Long> {

    //Find the favorite teams for a specific user
    List<favTeam> findByUserId(Long userId);

    //Check if the user already has a specific team as favorite
    Optional<favTeam> findByUserIdAndTeamId(Long userId, Long teamId);

    //Delete a specific favorite team
    void deleteByUserIdAndTeamId(Long userId, Long teamId);
}
