import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { ui } from "./ui";

const SIZE = 15;


const emptyBoardText = Array.from({ length: SIZE }, () =>
  ".".repeat(SIZE),
).join("\n");

function rowsToText(rows) {
  return (rows ?? []).join("\n");
}

function textToTiles(boardText) {
  const raw = (boardText ?? "").replace(/\r/g, ""); // handle Windows newlines
  const trimmed = raw.trim();

  // SHORTHAND: if they typed just a single word (no newlines), place it on center row
  if (!trimmed.includes("\n")) {
    const word = trimmed.toUpperCase().replace(/[^A-Z]/g, "");
    const tiles = [];
    if (!word) return tiles;

    const row = 7; // center row
    const startCol = Math.max(
      0,
      Math.min(15 - word.length, 7 - Math.floor(word.length / 2)),
    );

    for (let i = 0; i < word.length; i++) {
      tiles.push({ row, column: startCol + i, tile: word[i] });
    }
    return tiles;
  }

  // NORMAL MODE: 15x15 grid text
  const lines = raw.split("\n");
  const padded = Array.from({ length: SIZE }, (_, r) =>
    (lines[r] ?? "").padEnd(SIZE, ".").slice(0, SIZE),
  );

  const tiles = [];
  for (let r = 0; r < SIZE; r++) {
    for (let c = 0; c < SIZE; c++) {
      const ch = padded[r][c] ?? ".";
      const up = ch.toUpperCase();
      if (up >= "A" && up <= "Z") {
        tiles.push({ row: r, column: c, tile: up });
      }
    }
  }
  return tiles;
}

export default function BoardSolverPage() {
  const [rack, setRack] = useState("");
  const [board, setBoard] = useState(emptyBoardText);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  async function loadBoard() {
    const res = await fetch("/api/board");
    const json = await res.json();
    if (!res.ok) throw new Error(json?.message || `Failed ${res.status}`);
    setBoard(rowsToText(json.rows));
  }

  useEffect(() => {
    loadBoard().catch((e) => setError(e.message));
  }, []);

  async function handleReset() {
    setError("");
    setLoading(true);
    try {
      const res = await fetch("/api/board/reset", { method: "POST" });
      const json = await res.json();
      if (!res.ok) throw new Error(json?.message || `Failed ${res.status}`);
      setBoard(rowsToText(json.rows));
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  }

  async function handleApplyBoard() {
    console.log("APPLY CLICKED", { loading, boardLen: board.length });

    const trimmedBoard = (board ?? "").trimEnd();
    if (!trimmedBoard) {
      setError("Enter board layout");
      return;
    }

    setError("");
    setLoading(true);
    try {
      const tiles = textToTiles(trimmedBoard);
      console.log("BOARD TEXT:", JSON.stringify(trimmedBoard));
      console.log("TILES LENGTH:", tiles.length);
      console.log("PAYLOAD:", JSON.stringify({ tiles }).slice(0, 300));
      const res = await fetch("/api/board/tiles", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ tiles }),
      });

      const json = await res.json();
      if (!res.ok) throw new Error(json?.message || `Failed ${res.status}`);

      // use backend's canonical representation
      setBoard(rowsToText(json.rows));
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  }

  async function handleRefresh() {
    setError("");
    setLoading(true);
    try {
      await loadBoard();
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  }

  function handleSolve() {
    const trimmedRack = rack.trim();
    if (!trimmedRack) {
      setError("Enter rack tiles");
      return;
    }
    setError(
      "Solve not implemented yet: you don’t have /api/board/solve on the backend. Use Apply/Reset/Refresh to test the board for now.",
    );
  }

  return (
    <div className={ui.page}>
      <div className={ui.container}>
        <Link className={ui.back} to="/">
          Back
        </Link>

        <h1 className={`mt-6 ${ui.h1}`}>Board Solver</h1>

        <div className={`mt-6 ${ui.card}`}>
          <label className={ui.label} htmlFor="rack-input">
            Rack tiles (for later)
          </label>

          <div className="mt-2 flex gap-3">
            <input
              id="rack-input"
              className={ui.input}
              value={rack}
              onChange={(e) => setRack(e.target.value)}
              placeholder="e.g., TRADE?"
            />
            <button
              className={ui.button}
              onClick={handleSolve}
              disabled={loading}
            >
              Solve board
            </button>
          </div>

          <label className={`${ui.label} mt-6`} htmlFor="board-input">
            Board layout (15 lines × 15 chars). Letters are tiles; use '.' or
            '_' for empty.
          </label>

          <textarea
            id="board-input"
            className={`${ui.textarea} mt-2`}
            rows={15}
            value={board}
            onChange={(e) => setBoard(e.target.value)}
          />

          <div className="mt-3 flex flex-wrap gap-3">
            <button
              className={ui.button}
              onClick={handleApplyBoard}
              disabled={loading}
            >
              {loading ? "Applying..." : "Apply to backend"}
            </button>

            <button
              className={ui.back}
              onClick={handleReset}
              disabled={loading}
            >
              Reset board
            </button>

            <button
              className={ui.back}
              onClick={handleRefresh}
              disabled={loading}
            >
              Refresh from backend
            </button>
          </div>

          {error && <div className={ui.error}>{error}</div>}
        </div>
      </div>
    </div>
  );
}
