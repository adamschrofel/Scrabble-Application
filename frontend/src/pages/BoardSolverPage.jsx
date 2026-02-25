import { useEffect, useMemo, useRef, useState } from "react";
import PageShell from "../components/PageShell";
import ErrorBanner from "../components/ErrorBanner";
import { getBoard, resetBoard, setBoardTiles, solveBoard } from "../api/scrabbleApi";
import { ui } from "../ui";
import { SIZE, bonusAt } from "../board/bonusLayout";

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

          const type = bonusAt(r, c);
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
                "h-7 w-7 sm:h-8 sm:w-8 rounded-md border text-xs sm:text-sm font-extrabold",
                "flex items-center justify-center select-none",
                bg,
                ring,
                overlayGlow,
              ].join(" ")}
              title={
                type === "N"
                  ? ""
                  : type === "TW"
                    ? "Triple Word"
                    : type === "DW"
                      ? "Double Word"
                      : type === "TL"
                        ? "Triple Letter"
                        : "Double Letter"
              }
            >
              {letter}
            </button>
          );
        }),
      )}
    </div>
  );
}

export default function BoardSolverPage() {
  const [rows, setRows] = useState(emptyRows);
  const [rack, setRack] = useState("");
  const [bestPlays, setBestPlays] = useState(null);
  const [selectedPlayIdx, setSelectedPlayIdx] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const [selected, setSelected] = useState({ row: 7, column: 7 });
  const hiddenInputRef = useRef(null);

  const overlayTiles = useMemo(() => {
    const play = selectedPlayIdx == null ? null : bestPlays?.[selectedPlayIdx];
    return play?.tilesPlaced ?? [];
  }, [bestPlays, selectedPlayIdx]);

  async function loadBoard() {
    const json = await getBoard();
    setRows(json?.rows ?? emptyRows());
  }

  useEffect(() => {
    loadBoard().catch((e) => setError(e.message));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  function onSelect(r, c) {
    setSelected({ row: r, column: c });
    requestAnimationFrame(() => hiddenInputRef.current?.focus());
  }

  function setCell(r, c, ch) {
    setRows((prev) => {
      const next = [...prev];
      const row = (next[r] ?? ".".repeat(SIZE)).padEnd(SIZE, ".").slice(0, SIZE);
      const arr = row.split("");
      arr[c] = ch;
      next[r] = arr.join("");
      return next;
    });
  }

  function handleKeyDown(e) {
    const k = e.key;

    if (k === "ArrowUp" || k === "ArrowDown" || k === "ArrowLeft" || k === "ArrowRight") {
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

    if (k === "Backspace" || k === "Delete") {
      e.preventDefault();
      setCell(selected.row, selected.column, ".");
      return;
    }

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
      }
    }
  }

  async function withLoading(fn) {
    setLoading(true);
    setError("");
    try {
      await fn();
    } catch (e) {
      setError(e.message || "Request failed.");
    } finally {
      setLoading(false);
    }
  }

  async function handleReset() {
    await withLoading(async () => {
      setBestPlays(null);
      setSelectedPlayIdx(null);
      const json = await resetBoard();
      setRows(json?.rows ?? emptyRows());
    });
  }

  async function handleRefresh() {
    await withLoading(async () => {
      await loadBoard();
    });
  }

  async function handleApplyBoard() {
    await withLoading(async () => {
      const tiles = rowsToTiles(rows);
      const json = await setBoardTiles(tiles);
      setRows(json?.rows ?? emptyRows());
    });
  }

  async function handleSolve() {
    const trimmedRack = rack.trim();
    if (!trimmedRack) {
      setError("Enter rack tiles.");
      return;
    }

    await withLoading(async () => {
      setBestPlays(null);
      setSelectedPlayIdx(null);

      // Ensure backend is using our current board state.
      const tiles = rowsToTiles(rows);
      await setBoardTiles(tiles);

      const plays = await solveBoard({ rack: trimmedRack, limit: 50 });
      setBestPlays(Array.isArray(plays) ? plays : []);
      setSelectedPlayIdx(0);
    });
  }

  async function handleApplyPlayToBoard() {
    if (selectedPlayIdx == null || !bestPlays?.[selectedPlayIdx]) return;

    await withLoading(async () => {
      const play = bestPlays[selectedPlayIdx];

      // Apply play to local rows
      const nextRows = rows.slice();
      for (const t of play.tilesPlaced ?? []) {
        const r = t.row;
        const c = t.column;
        const ch = String(t.tile).toUpperCase();

        const row = (nextRows[r] ?? ".".repeat(SIZE)).padEnd(SIZE, ".").slice(0, SIZE);
        const arr = row.split("");
        arr[c] = ch;
        nextRows[r] = arr.join("");
      }

      // Send full board state as source of truth
      const json = await setBoardTiles(rowsToTiles(nextRows));
      setRows(json?.rows ?? nextRows);

      setBestPlays(null);
      setSelectedPlayIdx(null);
    });
  }

  const selectedPlay = selectedPlayIdx == null ? null : bestPlays?.[selectedPlayIdx];

  return (
    <div onMouseDown={() => hiddenInputRef.current?.focus()}>
      <PageShell
        title="Board Solver"
        subtitle="Click a square, type letters. Backspace/Delete clears. Arrow keys move."
      >
        <div className={`mt-6 ${ui.card}`}>
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
            <button className={ui.button} onClick={handleSolve} disabled={loading}>
              {loading ? "Working..." : "Solve"}
            </button>
          </div>

          <div className="mt-4 text-xs text-slate-400">
            Tip: Start with an empty board (Reset), type a few tiles, then Solve.
          </div>

          {/* Hidden input to capture keyboard typing for the board */}
          <input
            ref={hiddenInputRef}
            className="fixed left-0 top-0 h-px w-px opacity-0 pointer-events-none"
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
                </div>

                <div className="flex gap-2">
                  <button className={ui.back} onClick={handleReset} disabled={loading}>
                    Reset
                  </button>
                  <button className={ui.back} onClick={handleRefresh} disabled={loading}>
                    Refresh
                  </button>
                  <button className={ui.back} onClick={handleApplyBoard} disabled={loading}>
                    Apply
                  </button>
                </div>
              </div>

              <div className="mt-4">
                <BoardGrid rows={rows} selected={selected} onSelect={onSelect} overlayTiles={overlayTiles} />
              </div>

              <div className="mt-4 flex flex-wrap gap-3">
                {selectedPlay ? (
                  <button className={ui.button} onClick={handleApplyPlayToBoard} disabled={loading}>
                    {loading ? "Working..." : "Apply selected play"}
                  </button>
                ) : null}
              </div>

              <ErrorBanner message={error} />
            </div>

            <div>
              <div className={ui.h2}>Best plays</div>

              {!bestPlays ? (
                <div className="mt-2 text-sm text-slate-300">
                  Enter a rack and click <span className="font-semibold">Solve</span>.
                </div>
              ) : bestPlays.length === 0 ? (
                <div className="mt-2 text-sm text-slate-300">No plays found.</div>
              ) : (
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
                          <div className="text-sm text-slate-200">{p.score}</div>
                        </div>

                        {p.wordsFormed?.length ? (
                          <div className="mt-1 text-xs text-slate-400">
                            Words: {p.wordsFormed.join(", ")}
                          </div>
                        ) : null}
                      </button>
                    );
                  })}

                  <div className="mt-3 text-xs text-slate-400">
                    Selecting a play highlights placed tiles in green on the board.
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
      </PageShell>
    </div>
  );
}
