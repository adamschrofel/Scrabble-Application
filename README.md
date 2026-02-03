# Scrabble Solver / Word Finder

A full-stack Scrabble word finder and solver that helps players discover valid words, scores, and placements based on their rack and board state.

This project started as a Java command-line tool and has evolved into a web application with a Spring Boot backend and a React frontend.

---

## Features

- Find all valid Scrabble words from a given rack
- Supports blank tiles (`?` or `*`)
- Calculates Scrabble scores
- Board input support for more advanced solving
- Clean web UI built with React
- REST API backend built with Spring Boot

---

## Tech Stack

### Backend
- Java
- Spring Boot
- Maven
- Custom Scrabble dictionary (CSW)

### Frontend
- React
- Vite
- JavaScript / JSX
- CSS

---



## Running the Project Locally


npm run dev (from project root)

### Backend
```bash
git clone https://github.com/adamschrofel/Scrabble-Application.git
cd Scrabble-Application

npm run setup
npm run dev

This starts:

Spring Boot backend on http://localhost:8080

React frontend on http://localhost:5173
### Why this is important
- Shows you tested from a clean clone
- Signals professional repo hygiene
- Reduces recruiter friction to almost zero

---

## 2️⃣ Add a short “Project Status” section (signals maturity)
Near the bottom, add:

```md
## Project Status

This project is released as a **v1** focused on correctness and usability.
Performance optimizations and feature improvements are tracked in GitHub Issues.

Motivation


This project was built to:
    - Improve Java and algorithmic problem-solving skills
    - Learn Spring Boot and REST API design
    - Practice building a full-stack application from scratch
    - Create a portfolio project that combines logic, UX, and real-world constraints
    - Also… to help me continue to dominate my family at Scrabble.

Future Improvements
    - Dictionary definitions via Merriam-Webster API
    - Mobile UI improvements
    - Performance optimizations, currently quite slow solving board with 2+ words played(Currently slowed down by dictionary iteration for board solving, scans dictionary for every anchor point)


Author

Adam Schrofel
