package ca.adamschrofel.scrabble;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class ScrabbleController {

    @GetMapping("/solve")
    public Map<String, Object> solve(@RequestParam String tiles) throws Exception {
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("tiles", tiles);
        res.put("groups", Main.solve(tiles));
        return res;
    }
}
