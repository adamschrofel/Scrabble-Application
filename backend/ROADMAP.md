# Scrabble Solver Roadmap

This roadmap tracks the project from a rack-only solver → full board solver → polished frontend demo linked on my portfolio site.

---

## Milestone 0 — Repo hygiene + guardrails
- [ ] Standardize naming (e.g., `ScoredWord` vs `ScoreWord`) and packages
- [ ] Add a clear `README.md`:
  - [ ] how to run backend
  - [ ] how to run frontend
  - [ ] example API calls
- [ ] Add CI (GitHub Actions):
  - [ ] backend: `./mvnw test`
  - [ ] frontend: build + lint (if used)
- [ ] Confirm ruleset scope:
  - [ ] dictionary: CSW19
  - [ ] classic Scrabble 15×15 board layout
  - [ ] rack size: 7
  - [ ] blanks: `?` or `*`

---

## Milestone 1 — Core board engine (backend, no best-move search yet)
Goal: validate and score a single play correctly.

Board + placement basics
- [x] `Board` grid with get/set
- [x] `Board.place(Placement)` returns newly placed tiles
- [x] `Board.canPlace(Placement)` fit + no conflicts
- [x] `Board.isLegalPlacement(Placement)` first move center, later touches existing

Board layout multipliers
- [x] `BoardLayout` / `TileType` multipliers (at least center double word)

Scoring (Step 6)
- [x] Score a placement (main word only):
  - [x] letter values
  - [x] letter multipliers apply only to newly placed tiles
  - [x] word multipliers apply only for newly placed tiles’ squares
  - [x] bingo +50 if 7 tiles used
- [x] Unit tests for scoring (center DW, DL/TL cases)

Cross-words + dictionary legality (later)
- [ ] Detect cross-words created by newly placed tiles
- [ ] Validate all formed words exist in dictionary
- [ ] Score cross-words and add to total

Exit criteria:
- A placement can be evaluated as `{legal, score, wordsFormed}`.

---

## Milestone 2 — Backend API (stable contract)
Keep existing rack-only endpoint; add board-aware endpoints.

- [x] `/api/solve?tiles=...` rack-only words + scores
- [ ] `/api/evaluate` (board + placement):
  - request: board state + placement + rack (optional for later)
  - response: legal? score? main word + cross words? tilesUsed?
- [ ] `/api/bestmove` (board + rack):
  - response: list of top N moves with score and placement info

Exit criteria:
- Backend can evaluate and return a scored move.
- Backend can return top N suggested moves.

---

## Milestone 3 — Move generation (backend)
Goal: generate legal moves from board + rack and rank them by score.

Phase A: constrained (easy start)
- [ ] Anchor squares (adjacent to existing tiles)
- [ ] Generate candidate plays for ACROSS only
- [ ] Validate legality + dictionary + rack usage
- [ ] Score and return top N

Phase B: full
- [ ] Add DOWN direction
- [ ] First move: must cover center
- [ ] Performance improvements if needed:
  - [ ] prefix pruning / trie
  - [ ] early top-N pruning
  - [ ] caching / precomputed structures

Exit criteria:
- `/api/bestmove` returns top 10 plays quickly and correctly.

---

## Milestone 4 — Frontend: functional board UI
Goal: usable board editor + “get best move” button.

- [ ] Render 15×15 board
- [ ] Click to select squares; type letters
- [ ] Direction toggle (ACROSS/DOWN)
- [ ] Rack input field (supports `?` / `*`)
- [ ] Draft placement preview
- [ ] Buttons:
  - [ ] Evaluate/Score move
  - [ ] Place (commit)
  - [ ] Clear draft
  - [ ] Reset board
- [ ] Suggested moves list (top N)
  - [ ] click suggestion to preview and apply

Exit criteria:
- A user can recreate a position, enter rack, and see best move suggestions.

---

## Milestone 5 — Undo / redo + history
Goal: safe experimentation.

- [ ] Track history stack of committed moves
- [ ] Undo last committed move (revert tiles placed that turn)
- [ ] Redo (optional)
- [ ] Keep rack in sync on apply/undo

Exit criteria:
- No fear: all actions reversible.

---

## Milestone 6 — Visual polish (portfolio-ready)
Goal: looks like a real product.

- [ ] Bonus squares colored + labeled (DL/TL/DW/TW), center star
- [ ] Tile design (letter + score), blank tile UI
- [ ] Better layout (panel for rack + suggestions)
- [ ] Hover preview for suggested moves
- [ ] Mobile responsiveness (at least usable)
- [ ] Accessibility basics (tab focus, readable contrast)

Exit criteria:
- Demo lo
::contentReference[oaicite:0]{index=0}
