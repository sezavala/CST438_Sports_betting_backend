package com.cst438.project02.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import com.cst438.project02.entity.Team;
import com.cst438.project02.repository.TeamRepository;

@RestController
@RequestMapping("/team")
@CrossOrigin(origins = "https://project-02-20fa5120c543.herokuapp.com/")
public class TeamController {
    @Autowired
    private TeamRepository teamRepository;

    public TeamController(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @PostMapping("add") //for springboot to add a new team
    public ResponseEntity<?> addTeam(@RequestBody Team team) {
        if (teamRepository.existsByName(team.getName())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Team already exists");
        }
        teamRepository.save(team);
        return ResponseEntity.ok("Team added successfully");
    }
    @GetMapping("all") //for springboot to get all teams
    public ResponseEntity<?> getAllTeams() {
        return ResponseEntity.ok(teamRepository.findAll());
    }
    @GetMapping("id") //for springboot to get team by id
    public ResponseEntity<?> getTeamById(@PathVariable Long id) {
        Team existingTeam = teamRepository.findById(id).orElse(null);
        if (existingTeam == null) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Team not found");
        }
        return ResponseEntity.ok(existingTeam);
    }
    @PutMapping("update/{id}") //for springboot to update a team
    public ResponseEntity<?> updateTeam(@PathVariable Long id, @RequestBody Team teamDetails
    ) {
        Team existingTeam = teamRepository.findById(id).orElse(null);
        if (existingTeam == null) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Team not found");
        }
        existingTeam.setName(teamDetails.getName());
        existingTeam.setSport(teamDetails.getSport());
        existingTeam.setCity(teamDetails.getCity());
        teamRepository.save(existingTeam);
        return ResponseEntity.ok("Team updated successfully");
        
    }
    @DeleteMapping("delete/{id}") //for springboot to delete a team
    public ResponseEntity<?> deleteTeam(@PathVariable Long id) {
        Team existingTeam = teamRepository.findById(id).orElse(null);
        if (existingTeam == null) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Team not found");
        }
        teamRepository.delete(existingTeam);
        return ResponseEntity.ok("Team deleted successfully");
    }

    public TeamRepository getTeamRepository() {
        return teamRepository;
    }

    public void setTeamRepository(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }
}
