# Scrabble Solver / Word Finder

A full-stack Scrabble helper that can:
- generate all valid words from a rack (with blanks)
- validate words + return definitions (CSW19)
- solve a board state and suggest best plays

This project started as a Java command-line tool and has evolved into a web application with a Spring Boot backend and a React frontend.
---

## Features

- **Rack solving**: find valid words grouped by length, with scores
- **Board solving**: suggest best placements based on current board state
- **Word definitions**: CSW19-based definitions lookup (Currently working on integrating newer version)
- **Blank tiles**: supports `?` or `*` as wildcards
---

## Tech Stack

### Backend
- Java
- Spring Boot
- Maven

### Frontend
- React
- React Router
- Vite
- JavaScript / JSX
- Tailwind CSS

---

## Architecture

High-level flow:

Frontend (React)  →  REST API (Spring Boot)  →  Solver + Dictionary

Backend packages (simplified):
- `controller`: REST endpoints (`/api/rack`, `/api/board`, `/api/words`)
- `service`: orchestration + board state management
- `solver`: move generation + evaluation
- `dictionary`: trie + definition loading
- `dto`: request/response types (API boundary)

## API (Routes)

- `GET /api/rack/solve?rack=ABCDE??` → rack solve results
- `GET /api/words/{word}` → definition payload
- `GET /api/board` → current board state
- `POST /api/board/reset` → clear board
- `POST /api/board/tiles` → set multiple tiles
- `POST /api/board/solve` → best plays for current board + rack

## Deployment Notes

This repo is set up for separate deployments (frontend and backend).

### Frontend environment

The frontend can target either:
- **Local dev proxy** (default): requests go to `/api/*` and Vite proxies to `http://localhost:8080`
- **Deployed backend**: set `VITE_API_BASE_URL` to your backend origin

Example:
- `frontend/.env`:
  - `VITE_API_BASE_URL=https://your-backend-host`

A template is included at `frontend/.env.example`.


### Backend environment

The backend supports common hosting environments:
- `PORT` (defaults to 8080)
- `CORS_ALLOWED_ORIGINS` (comma-separated frontend origins)

Example:
- `PORT=8080`
- `CORS_ALLOWED_ORIGINS=http://localhost:5173,https://your-frontend-host`

## Run Locally

### 1) Backend

```bash
cd backend
./mvnw spring-boot:run
```

Backend runs on `http://localhost:8080`.

### 2) Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend runs on `http://localhost:5173` and proxies `/api/*` to the backend.

### (Optional) Run both with one command

From the repo root:

```bash
npm install
npm run dev
```

## CI

GitHub Actions runs:
- backend: `mvn test`
- frontend: `npm run build`

Workflow: `.github/workflows/ci.yml`

## Future Improvements

- Basic unit/integration test coverage for solver + controllers
- Performance instrumentation for board solving (timings + caching)
- Persist board state per user/session instead of server-global state
- Dictionary definitions via Merriam-Webster API
- UI Improvements

---

Author: Adam Schrofel
