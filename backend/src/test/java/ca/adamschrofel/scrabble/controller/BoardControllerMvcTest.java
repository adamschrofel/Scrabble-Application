package ca.adamschrofel.scrabble.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ca.adamschrofel.scrabble.exceptions.GlobalExceptionHandler;
import ca.adamschrofel.scrabble.service.BoardService;
import ca.adamschrofel.scrabble.service.ScrabbleService;

@WebMvcTest(BoardController.class)
@Import(GlobalExceptionHandler.class)
class BoardControllerMvcTest {

    @Autowired MockMvc mvc;

    @MockitoBean ScrabbleService service;
    @MockitoBean BoardService board;

    private static String[] emptyRows() {
        String row = ".".repeat(15);
        String[] rows = new String[15];
        for (int i = 0; i < 15; i++) rows[i] = row;
        return rows;
    }

    @Test
    void getBoard_returnsState() throws Exception {
        when(board.rowsAsStrings()).thenReturn(emptyRows());

        mvc.perform(get("/api/board"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(15))
                .andExpect(jsonPath("$.rows[0]").value(".".repeat(15)));
    }

    @Test
    void setTile_nullBody_returns400Json() throws Exception {
        mvc.perform(post("/api/board/tile").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"));
    }
}
