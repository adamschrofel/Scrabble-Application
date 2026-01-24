package ca.adamschrofel.scrabble;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class ScrabbleController {
    private final ScrabbleService service;

    public ScrabbleController(ScrabbleService service){
        this.service = service;
    }


    @GetMapping("/api/solve")
    public Map<String, Object> solve(@RequestParam String tiles) throws Exception {
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("tiles", tiles);
        res.put("groups", service.solve(tiles));
        return res;
    }
}
