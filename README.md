# Scrabble Solver / Word Finder

A production-deployed, full-stack Scrabble helper that can:

- Generate all valid words from a rack (with blank support)
- Validate words and return CSW19 definitions
- Solve a board state and suggest optimal plays
- Score placements according to official Scrabble rules

Originally built as a Java CLI tool, this project evolved into a deployed web application with a Spring Boot backend and a React frontend.

---

## Live Deployment

Frontend: https://scrabble-helpers.vercel.app  
Backend: Hosted on Render

---

## Core Features

### Rack Solver
- Generates all valid words from a rack
- Groups results by word length
- Scores each word
- Supports blank tiles (`?` or `*`)

### Board Solver
- Evaluates legal placements from current board state
- Scores moves including cross-words
- Returns top plays ranked by score
- Handles blank assignments dynamically

### Word Validation & Definitions
- CSW19 dictionary(Will be updated to newest CSW in future)
- Fast membership checks
- Definition lookup endpoint

---

## Performance Architecture

### Trie-Based Dictionary

The dictionary is implemented using a **Trie (prefix tree)** data structure.

Key benefits:

- O(L) lookup time for word validation (L = word length)
- Efficient prefix pruning during rack and board generation
- Dramatically reduced candidate search space
- Scalable move generation without scanning the entire dictionary

The solver incrementally builds words using Trie traversal, significantly improving performance during board evaluation compared to original dictionary iteration.

---

## Tech Stack

### Backend
- Java
- Spring Boot
- Maven
- Custom Trie implementation
- REST API architecture
- Global CORS configuration

### Frontend
- React
- React Router
- Vite
- JavaScript / JSX
- Tailwind CSS

### Hosting
- Frontend: Vercel
- Backend: Render

---

## Architecture Overview

```
Frontend (React SPA)
        ↓
REST API (Spring Boot)
        ↓
Solver Engine + Trie Dictionary
```

### Backend Package Structure

- `controller` – REST endpoints (`/api/rack`, `/api/board`, `/api/words`)
- `service` – orchestration + board state handling
- `solver` – move generation + scoring engine
- `dictionary` – Trie implementation + definition loading
- `dto` – request/response contracts

---

## API Routes

### Rack Solver
- `GET /api/rack/solve?rack=ABCDE??`

### Definitions
- `GET /api/words/{word}`

### Board State
- `GET /api/board`
- `POST /api/board/reset`
- `POST /api/board/tiles`

### Board Solve
- `POST /api/board/solve`

---

## Deployment Configuration

This project uses separate frontend and backend deployments.

### Frontend Environment

The frontend uses `VITE_API_BASE_URL` to determine the backend origin.

Example (`frontend/.env`):

```
VITE_API_BASE_URL=https://scrabble-application-backend.onrender.com
```

If unset, local development proxy is used.

---

### Backend Environment

Supported environment variables:

- `PORT`
- `APP_CORS_ALLOWED_ORIGINS`

Example (Render environment settings):

```
PORT=8080
APP_CORS_ALLOWED_ORIGINS=https://scrabble-helpers.vercel.app
```

Global CORS configuration ensures the deployed frontend can access the backend API.

---

## Run Locally

### Backend

```bash
cd backend
./mvnw spring-boot:run
```

Runs at: http://localhost:8080

---

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Runs at: http://localhost:5173

During development, Vite proxies `/api/*` to the backend.

---

## CI

GitHub Actions runs:

- Backend: `mvn test`
- Frontend: `npm run build`

Workflow: `.github/workflows/ci.yml`

---

## Versioning

- `v1.1.0` – Trie-based dictionary + production deployment

---

## Future Improvements

- Solver performance instrumentation & caching
- Persistent board state (database-backed)
- Expanded test coverage
- Updated dictionary versions
- Advanced move heuristics (leave-value evaluation)
- Improved UI


---

Author: Adam Schrofel
