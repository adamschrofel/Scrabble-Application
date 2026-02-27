package ca.adamschrofel.scrabble.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ca.adamschrofel.scrabble.service.DefinitionService;

@WebMvcTest(WordController.class)
class WordControllerMvcTest {

    @Autowired MockMvc mvc;

    @MockitoBean DefinitionService definitions;

    @Test
    void define_foundWord() throws Exception {
        when(definitions.getDefinition("HELLO")).thenReturn("A greeting.");

        mvc.perform(get("/api/words/hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.word").value("HELLO"))
                .andExpect(jsonPath("$.found").value(true))
                .andExpect(jsonPath("$.definition").value("A greeting."));
    }

    @Test
    void define_missingWord() throws Exception {
        when(definitions.getDefinition("XYZ")).thenReturn(null);

        mvc.perform(get("/api/words/xyz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.word").value("XYZ"))
                .andExpect(jsonPath("$.found").value(false))
                .andExpect(jsonPath("$.definition").doesNotExist());
    }
}
