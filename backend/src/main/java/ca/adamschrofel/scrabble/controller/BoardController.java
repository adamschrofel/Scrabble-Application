package ca.adamschrofel.scrabble.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.adamschrofel.scrabble.board.Board;
import ca.adamschrofel.scrabble.dto.BestPlay;
import ca.adamschrofel.scrabble.dto.BoardSolveRequest;
import ca.adamschrofel.scrabble.dto.BoardState;
import ca.adamschrofel.scrabble.dto.BoardTileUpdate;
import ca.adamschrofel.scrabble.dto.BoardUpdateRequest;
import ca.adamschrofel.scrabble.exceptions.InvalidTilesException;
import ca.adamschrofel.scrabble.service.BoardService;
import ca.adamschrofel.scrabble.service.ScrabbleService;

@RestController
@RequestMapping("/api/board")
public class BoardController {

    private final ScrabbleService service;
    private final BoardService board;

    public BoardController(ScrabbleService service, BoardService board) {
        this.service = service;
        this.board = board;
    }

    /**
     * Board solver (best plays) using the current server-side board state.
     *
     * <p>
     * Route: {@code POST /api/board/solve}
     * </p>
     */
    @PostMapping("/solve")
    public List<BestPlay> solveBoard(@RequestBody BoardSolveRequest request) throws InvalidTilesException {
        String rack = request == null ? null : request.rack();
        int limit = (request != null && request.limit() != null) ? request.limit() : 25;

        Board current = board.snapshotBoard();
        return service.bestPlays(current, rack, limit);
    }

    @GetMapping
    public BoardState getBoard() {
        return new BoardState(BoardService.SIZE, List.of(board.rowsAsStrings()));
    }

    @PostMapping("/reset")
    public BoardState resetBoard() {
        board.reset();
        return getBoard();
    }

    @SuppressWarnings("null")
    @PostMapping("/tile")
    public BoardState setTile(@RequestBody BoardTileUpdate req) {
        char tile = normalizeTile(req == null ? null : req.tile());
        board.set(req.row(), req.column(), tile);
        return getBoard();
    }

    @PostMapping("/tiles")
    public BoardState setTiles(@RequestBody BoardUpdateRequest req) {
        if (req != null && req.tiles() != null) {
            for (BoardTileUpdate t : req.tiles()) {
                char tile = normalizeTile(t.tile());
                board.set(t.row(), t.column(), tile);
            }
        }
        return getBoard();
    }

    private char normalizeTile(String tile) {
        if (tile == null)
            return '.';
        String s = tile.trim();
        if (s.isEmpty() || s.equals("."))
            return '.';
        char ch = Character.toUpperCase(s.charAt(0));
        return ch;
    }
}
