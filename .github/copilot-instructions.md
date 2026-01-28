# Scrabble Application - AI Agent Instructions

## Project Overview
Full-stack Scrabble word solver application: Java Spring Boot REST API backend (port 8080) + React frontend with Vite (dev proxy to backend at `/api`). Users input tile racks to find valid Scrabble words and get word definitions from CSW19 dictionary.

## Architecture & Data Flow

**Backend (Spring Boot 4.0.2, Java 17)**
- **ScrabbleController**: REST endpoints for `/api/solve` and `/api/define`
  - `solve(tiles)` → validates input → calls ScrabbleService → returns LengthGroups JSON
  - `define(word)` → queries DefinitionService → returns word definition
- **ScrabbleService**: Orchestrates word-finding logic via WordFinder
- **WordFinder**: Core algorithm - loads CSW19 dictionary into `wordsByLength[]` array (16 slots for word lengths 1-15), filters by rack constraints using blank tile logic
- **Rack**: Immutable value object - parses tile string, tracks letter counts and blank tiles (`?` or `*`)
- **DefinitionService**: Maps CSW19 definitions file into memory HashMap, queried by word

**Frontend (React 19 + Vite)**
- **App.jsx**: Routes `/` (WordFinderPage) and `/define/:word` (DefinitionPage)
- **WordFinderPage.jsx**: Managed state (tiles, data, error), fetch calls to `/api/solve`, displays results as LengthGroups with clickable word links
- Vite dev proxy routes `/api/*` to localhost:8080 backend

## Critical Workflows

### Building & Running
```bash
# Backend (port 8080)
cd backend
mvn clean install          # Compile & run tests
mvn spring-boot:run        # Start server

# Frontend (port 5173 dev server)
cd frontend
npm install
npm run dev
```

### Testing
- Backend uses Spring Boot Test + JUnit 5 (minimal test coverage currently)
- Frontend has no test setup configured
- Run: `mvn test` (backend only)

## Key Patterns & Conventions

### Backend Patterns
1. **Word Filtering Logic**: WordFinder.makesWord() & blanksTracker() - complex algorithm matching word letters to rack tiles, with multi-blank positioning logic. Blanks = wildcard tiles that substitute missing letters.
2. **Tile Scoring**: Static class ScrabbleScoring with hardcoded point values (A=1, Q/Z=10, etc.)
3. **Input Validation**: InputValidator.normalizeTiles() converts input to uppercase, strips whitespace, rejects non-A-Z/blank chars, limits to 15 tiles. Throws InvalidTilesException caught by GlobalExceptionHandler.
4. **Service Layer**: Services use constructor injection (Spring autowiring), load dictionary files in constructor
5. **Exception Handling**: GlobalExceptionHandler.handleInvalidTiles() returns {error: "INVALID_TILES", message: "..."} on BAD_REQUEST

### Frontend Patterns
1. **State Lifting**: Parent App.jsx holds all state, passes as props (tiles, data, error)
2. **Async Fetch**: WordFinderPage.handleSolve() uses native fetch (no Axios), URL encodes tiles, checks response.ok before parsing JSON
3. **Error Handling**: Displays error messages inline, clears data on new search
4. **Styling**: Tailwind CSS utility classes + custom ui.js module for reusable class names
5. **Navigation**: React Router for multi-page SPA, route to `/define/:word` on word click

### Project-Specific Naming
- "Blank tiles" = `?` or `*` (Scrabble wildcards)
- "Rack" = player's current hand of tiles (typically 7-15)
- "LengthGroup" = {length, words[]} - results grouped by word length (descending)
- Dictionary path: `dictionary/csw19Words.txt` (word list) and `dictionary/csw19Definitions.txt`

## Integration Points & Dependencies
1. **API Contract**:
   - GET `/api/solve?tiles=TRIEDEST` → `{tiles: "TRIEDEST", groups: [{length: 5, words: [...]}, ...]}`
   - GET `/api/define?word=TRIED` → `{word: "TRIED", found: true, definition: "..."}` (null definition if not found)
2. **Dictionary Files**: Loaded at service startup, must be UTF-8 TSV format (word\tdefinition)
3. **Exceptions**: Only InvalidTilesException is caught globally; other exceptions bubble as 500

## Common Development Tasks

**Add new endpoint**: Create method in ScrabbleController with @GetMapping, inject required services
**Fix word-finding bug**: Debug in WordFinder.makesWord() and blanksTracker() - focus on array indexing and blank allocation logic
**Update dictionary**: Replace CSV/TSV files in `src/main/resources/dictionary/`, service reloads on restart
**Frontend UI changes**: Edit WordFinderPage.jsx or add route in App.jsx, use ui.js classes
**Add word scoring**: Use ScrabbleScoring.scoreWord(String) static method - maps to JSON if needed in controller

## Notes for AI Agents
- WordFinder constructor loads entire dictionary into memory at startup - keep this in mind for scalability concerns
- Blank tile logic has complex edge cases (2+ blanks per word, repeated letters)
- Frontend assumes backend always returns well-formed JSON even on errors (relies on GlobalExceptionHandler)
- No authentication/authorization implemented
- CSW19 is the official Scrabble dictionary reference - do not swap without coordination
