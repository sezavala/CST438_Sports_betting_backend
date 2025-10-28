package com.cst438.project02;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class TeamTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void getAllTeams() throws Exception {
        mvc.perform(get("/team/all"))
           .andExpect(status().isOk())
           .andExpect(content().contentType("application/json"));
    }

    @Test
    public void getTeamById() throws Exception {
        mvc.perform(get("/team/id/1"))
           .andExpect(status().isOk())
           .andExpect(content().contentType("application/json"));
    }

}
