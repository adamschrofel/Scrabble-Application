import { useEffect, useMemo, useRef, useState } from "react";
import { Link } from "react-router-dom";
import { ui } from "./ui";

const SIZE = 15;

/**
 * Frontend-only bonus layout for visuals.
 * Must match backend BoardLayout.standardLayout().
 */
function tileTypeAt(row, col) {
  const key = `${row},${col}`;
  const TW = new Set([
    "0,0",
    "0,7",
    "0,14",
    "7,0",
    "7,14",
    "14,0",
    "14,7",
    "14,14",
  ]);
  const DW = new Set([
    "1,1",
    "2,2",
    "3,3",
    "4,4",
    "7,7",
    "10,10",
    "11,11",
    "12,12",
    "13,13",
    "1,13",
    "2,12",
    "3,11",
    "4,10",
    "10,4",
    "11,3",
    "12,2",
    "13,1",
  ]);
  const TL = new Set([
    "1,5",
    "1,9",
    "5,1",
    "5,5",
    "5,9",
    "5,13",
    "9,1",
    "9,5",
    "9,9",
    "9,13",
    "13,5",
    "13,9",
  ]);
  const DL = new Set([
    "0,3",
    "0,11",
    "2,6",
    "2,8",
    "3,0",
    "3,7",
    "3,14",
    "6,2",
    "6,6",
    "6,8",
    "6,12",
    "7,3",
    "7,11",
    "8,2",
    "8,6",
    "8,8",
    "8,12",
    "11,0",
    "11,7",
    "11,14",
    "12,6",
    "12,8",
    "14,3",
    "14,11",
  ]);

  if (TW.has(key)) return "TW";
  if (DW.has(key)) return "DW";
  if (TL.has(key)) return "TL";
  if (DL.has(key)) return "DL";
  return "N";
}

function emptyRows() {
  return Array.from({ length: SIZE }, () => ".".repeat(SIZE));
}

function rowsToTiles(rows) {
  const tiles = [];
  for (let r = 0; r < SIZE; r++) {
    const row = rows[r] ?? ".".repeat(SIZE);
    for (let c = 0; c < SIZE; c++) {
      const ch = (row[c] ?? ".").toUpperCase();
      if (ch >= "A" && ch <= "Z") tiles.push({ row: r, column: c, tile: ch });
    }
  }
  return tiles;
}

function clamp(n, min, max) {
  return Math.max(min, Math.min(max, n));
}

function BoardGrid({ rows, selected, onSelect, overlayTiles }) {
  const overlayMap = useMemo(() => {
    const m = new Map();
    for (const t of overlayTiles ?? []) m.set(`${t.row},${t.column}`, t.tile);
    return m;
  }, [overlayTiles]);

  return (
    <div
      className="inline-grid gap-1"
      style={{ gridTemplateColumns: `repeat(${SIZE}, minmax(0, 1fr))` }}
    >
      {Array.from({ length: SIZE }).map((_, r) =>
        Array.from({ length: SIZE }).map((__, c) => {
          const base = (rows?.[r]?.[c] ?? ".").toUpperCase();
          const overlay = overlayMap.get(`${r},${c}`);
          const letter = overlay ?? (base === "." ? "" : base);

          const type = tileTypeAt(r, c);

          const isSelected = selected?.row === r && selected?.column === c;
          const isOverlay = !!overlay;

          const bg =
            type === "TW"
              ? "bg-rose-700/40 border-rose-500/40"
              : type === "DW"
                ? "bg-rose-500/25 border-rose-400/30"
                : type === "TL"
                  ? "bg-sky-700/35 border-sky-500/40"
                  : type === "DL"
                    ? "bg-sky-500/20 border-sky-400/30"
                    : "bg-slate-950 border-slate-800";

          const ring = isSelected ? "ring-2 ring-violet-500" : "";
          const overlayGlow = isOverlay ? "ring-2 ring-emerald-400/70" : "";

          return (
            <button
              key={`${r}-${c}`}
              type="button"
              onClick={() => onSelect(r, c)}
              className={[
                "relative aspect-square w-8 sm:w-9 md:w-10 select-none",
                "rounded-lg border",
                "flex items-center justify-center",
                "font-mono font-extrabold text-slate-100",
                "focus:outline-none",
                bg,
                ring,
                overlayGlow,
              ].join(" ")}
              title={`(${r + 1}, ${c + 1}) ${type}`}
            >
              {letter ? (
                <span className="text-base sm:text-lg">{letter}</span>
              ) : (
                <span className="absolute bottom-1 right-1 text-[9px] text-slate-400">
                  {type === "N" ? "" : type}
                </span>
              )}
            </button>
          );
        }),
      )}
    </div>
  );
}

export default function BoardSolverPage() {
  const [rack, setRack] = useState("");
  const [rows, setRows] = useState(emptyRows());
  const [selected, setSelected] = useState({ row: 7, column: 7 });

  const [bestPlays, setBestPlays] = useState(null);
  const [selectedPlayIdx, setSelectedPlayIdx] = useState(null);

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const hiddenInputRef = useRef(null);

  const overlayTiles = useMemo(() => {
    if (selectedPlayIdx == null || !bestPlays?.[selectedPlayIdx]) return [];
    return bestPlays[selectedPlayIdx].tilesPlaced ?? [];
  }, [bestPlays, selectedPlayIdx]);

  async function loadBoard() {
    const res = await fetch("/api/board");
    const json = await res.json();
    if (!res.ok) throw new Error(json?.message || `Failed ${res.status}`);
    setRows(json.rows ?? emptyRows());
  }

  useEffect(() => {
    loadBoard().catch((e) => setError(e.message));
  }, []);

  function onSelect(r, c) {
    setSelected({ row: r, column: c });
    // keep keyboard typing smooth
    requestAnimationFrame(() => hiddenInputRef.current?.focus());
  }

  function setCell(r, c, ch) {
    setRows((prev) => {
      const next = [...prev];
      const row = (next[r] ?? ".".repeat(SIZE))
        .padEnd(SIZE, ".")
        .slice(0, SIZE);
      const arr = row.split("");
      arr[c] = ch;
      next[r] = arr.join("");
      return next;
    });
  }

  function handleKeyDown(e) {
    const k = e.key;

    // movement
    if (
      k === "ArrowUp" ||
      k === "ArrowDown" ||
      k === "ArrowLeft" ||
      k === "ArrowRight"
    ) {
      e.preventDefault();
      setSelected((s) => {
        const dr = k === "ArrowUp" ? -1 : k === "ArrowDown" ? 1 : 0;
        const dc = k === "ArrowLeft" ? -1 : k === "ArrowRight" ? 1 : 0;
        return {
          row: clamp(s.row + dr, 0, SIZE - 1),
          column: clamp(s.column + dc, 0, SIZE - 1),
        };
      });
      return;
    }

    // clear
    if (k === "Backspace" || k === "Delete") {
      e.preventDefault();
      setCell(selected.row, selected.column, ".");
      return;
    }

    // letters
    if (k.length === 1) {
      const up = k.toUpperCase();
      if (up >= "A" && up <= "Z") {
        e.preventDefault();
        setCell(selected.row, selected.column, up);
        setSelected((s) => ({
          row: s.row,
          column: clamp(s.column + 1, 0, SIZE - 1),
        }));
        return;
      }
      if (k === "." || k === "_") {
        e.preventDefault();
        setCell(selected.row, selected.column, ".");
        return;
      }
    }
  }

  async function handleReset() {
    setError("");
    setBestPlays(null);
    setSelectedPlayIdx(null);
    setLoading(true);
    try {
      const res = await fetch("/api/board/reset", { method: "POST" });
      const json = await res.json();
      if (!res.ok) throw new Error(json?.message || `Failed ${res.status}`);
      setRows(json.rows ?? emptyRows());
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  }

  async function handleApplyBoard() {
    setError("");
    setLoading(true);
    try {
      const tiles = rowsToTiles(rows);
      const res = await fetch("/api/board/tiles", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ tiles }),
      });

      const json = await res.json();
      if (!res.ok) throw new Error(json?.message || `Failed ${res.status}`);
      setRows(json.rows ?? emptyRows());
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

  async function handleSolve() {
    const trimmedRack = rack.trim();
    if (!trimmedRack) {
      setError("Enter rack tiles");
      return;
    }

    setError("");
    setLoading(true);
    setBestPlays(null);
    setSelectedPlayIdx(null);

    try {
      // Make sure backend has our current board
      const tiles = rowsToTiles(rows);
      const push = await fetch("/api/board/tiles", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ tiles }),
      });
      if (!push.ok) {
        const j = await push.json().catch(() => null);
        throw new Error(j?.message || `Failed ${push.status}`);
      }

      const res = await fetch(
        `/api/bestplays?tiles=${encodeURIComponent(trimmedRack)}&limit=50`,
      );
      const json = await res.json();
      if (!res.ok) throw new Error(json?.message || `Failed ${res.status}`);

      setBestPlays(Array.isArray(json) ? json : []);
      setSelectedPlayIdx(0);
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  }

  async function handleApplyPlayToBoard() {
    if (selectedPlayIdx == null || !bestPlays?.[selectedPlayIdx]) return;

    setError("");
    setLoading(true);
    try {
      const play = bestPlays[selectedPlayIdx];

      // 1) Apply play to our local rows to form the new board state
      const nextRows = rows.slice();
      for (const t of play.tilesPlaced ?? []) {
        const r = t.row;
        const c = t.column;
        const ch = String(t.tile).toUpperCase();

        const row = (nextRows[r] ?? ".".repeat(SIZE))
          .padEnd(SIZE, ".")
          .slice(0, SIZE);
        const arr = row.split("");
        arr[c] = ch;
        nextRows[r] = arr.join("");
      }

      // 2) Send the FULL board state to backend (not just the placed tiles)
      const tiles = rowsToTiles(nextRows);
      const res = await fetch("/api/board/tiles", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ tiles }),
      });

      const json = await res.json();
      if (!res.ok) throw new Error(json?.message || `Failed ${res.status}`);

      // 3) Update UI from backend (source of truth)
      setRows(json.rows ?? nextRows);
      setBestPlays(null);
      setSelectedPlayIdx(null);
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  }

  const selectedPlay =
    selectedPlayIdx == null ? null : bestPlays?.[selectedPlayIdx];

  return (
    <div
      className={ui.page}
      onMouseDown={() => hiddenInputRef.current?.focus()}
    >
      <div className={ui.container}>
        <Link className={ui.back} to="/">
          Back
        </Link>

        <h1 className={`mt-6 ${ui.h1}`}>Board Solver</h1>
        <div className={ui.card}>
          <label className={ui.label} htmlFor="rack-input">
            Rack tiles
          </label>

          <div className="mt-2 flex gap-3">
            <input
              id="rack-input"
              className={ui.input}
              value={rack}
              onChange={(e) => setRack(e.target.value)}
              placeholder="e.g., TRADE? (use ?/* for blanks)"
            />
            <button
              className={ui.button}
              onClick={handleSolve}
              disabled={loading}
            >
              {loading ? "Working..." : "Solve"}
            </button>
          </div>

          <div className="mt-4 text-xs text-slate-400">
            Tip: Start with an empty board (Reset), type a few tiles, then
            Solve.
          </div>

          <div className="mt-6">
            <div className={ui.h2}>Best plays</div>

            {!bestPlays && (
              <div className="mt-2 text-sm text-slate-300">
                Enter a rack and click{" "}
                <span className="font-semibold">Solve</span>.
              </div>
            )}

            {/* Hidden input to capture keyboard typing for the board */}
            <input
              ref={hiddenInputRef}
              className="fixed left-0 top-0 w-px h-px opacity-0 pointer-events-none"
              value=""
              onChange={() => {}}
              onKeyDown={handleKeyDown}
              aria-hidden="true"
            />

            <div className="mt-6 grid gap-6 lg:grid-cols-[auto,1fr] items-start">
              <div className={ui.card}>
                <div className="flex items-center justify-between gap-3">
                  <div>
                    <div className={ui.h2}>Board</div>
                    <div className="mt-1 text-xs text-slate-400">
                      Click a square, then type letters. Backspace/Delete
                      clears. Arrow keys move.
                    </div>
                  </div>

                  <div className="flex gap-2">
                    <button
                      className={ui.back}
                      onClick={handleReset}
                      disabled={loading}
                    >
                      Reset
                    </button>
                    <button
                      className={ui.back}
                      onClick={handleRefresh}
                      disabled={loading}
                    >
                      Refresh
                    </button>
                  </div>
                </div>

                <div className="mt-4">
                  <BoardGrid
                    rows={rows}
                    selected={selected}
                    onSelect={onSelect}
                    overlayTiles={overlayTiles}
                  />
                </div>

                <div className="mt-4 flex flex-wrap gap-3">
                  {selectedPlay && (
                    <button
                      className={ui.button}
                      onClick={handleApplyPlayToBoard}
                      disabled={loading}
                    >
                      {loading ? "Working..." : "Apply selected play"}
                    </button>
                  )}
                </div>

                {error && <div className={ui.error}>{error}</div>}
              </div>

              {bestPlays && bestPlays.length === 0 && (
                <div className="mt-2 text-sm text-slate-300">
                  No plays found.
                </div>
              )}

              {bestPlays && bestPlays.length > 0 && (
                <div className="mt-3 grid gap-2">
                  {bestPlays.slice(0, 20).map((p, idx) => {
                    const isSel = idx === selectedPlayIdx;
                    const pl = p.placement;
                    const label = `${pl.word} @ (${pl.row + 1},${pl.column + 1}) ${pl.direction}`;
                    return (
                      <button
                        key={idx}
                        type="button"
                        onClick={() => setSelectedPlayIdx(idx)}
                        className={[
                          "w-full text-left rounded-xl border px-3 py-2",
                          isSel
                            ? "border-violet-500 bg-violet-950/40"
                            : "border-slate-800 bg-slate-950 hover:bg-slate-900",
                        ].join(" ")}
                      >
                        <div className="flex items-center justify-between gap-3">
                          <div className="font-semibold">{label}</div>
                          <div className="text-sm text-slate-200">
                            {p.score}
                          </div>
                        </div>

                        {p.wordsFormed?.length ? (
                          <div className="mt-1 text-xs text-slate-400">
                            Words: {p.wordsFormed.join(", ")}
                          </div>
                        ) : null}
                      </button>
                    );
                  })}
                </div>
              )}

              {bestPlays && bestPlays.length > 0 && (
                <div className="mt-3 text-xs text-slate-400">
                  Selecting a play highlights placed tiles in green on the
                  board.
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
