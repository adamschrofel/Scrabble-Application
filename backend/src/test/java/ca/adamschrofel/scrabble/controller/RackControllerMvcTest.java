package ca.adamschrofel.scrabble.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ca.adamschrofel.scrabble.exceptions.GlobalExceptionHandler;
import ca.adamschrofel.scrabble.rack.LengthGroup;
import ca.adamschrofel.scrabble.service.ScrabbleService;

@WebMvcTest(RackController.class)
@Import(GlobalExceptionHandler.class)
class RackControllerMvcTest {

    @Autowired MockMvc mvc;

    @MockitoBean ScrabbleService service;

    @Test
    void solveRack_returnsNormalizedTilesAndGroups() throws Exception {
        when(service.solve("HI")).thenReturn(List.of(new LengthGroup(2, List.of("HI"))));

        mvc.perform(get("/api/rack/solve").param("rack", "hi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tiles").value("HI"))
                .andExpect(jsonPath("$.groups[0].length").value(2))
                .andExpect(jsonPath("$.groups[0].words[0].word").value("HI"))
                .andExpect(jsonPath("$.groups[0].words[0].score").value(5));
    }

    @Test
    void solveRack_invalidRack_returns400Json() throws Exception {
        mvc.perform(get("/api/rack/solve").param("rack", "A"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("INVALID_TILES"))
                .andExpect(jsonPath("$.message").exists());
    }
}
