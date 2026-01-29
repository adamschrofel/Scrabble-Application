import { useState } from "react";
import { Link } from "react-router-dom";
import { ui } from "./ui";

const emptyBoard = Array.from({ length: 15 }, () => "_".repeat(15)).join("\n");

export default function BoardSolverPage() {
  const [rack, setRack] = useState("");
  const [board, setBoard] = useState(emptyBoard);
  const [result, setResult] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  async function handleSolve() {
    const trimmedRack = rack.trim();
    const trimmedBoard = board.trim();

    if (!trimmedRack) {
      setError("Enter rack tiles");
      setResult(null);
    }

    if (!trimmedBoard) {
      setError("Enter board layout");
      setResult(null);
    }

    setError("");
    setLoading(true);
    setResult(null);

    try {
      const res = await fetch("//api/board/solve", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          rack: trimmedRack,
          board: trimmedBoard,
        }),
      });

      const json = await res.json();

      if (!res.ok) {
        throw new Error(json?.message || `Failed ${res.status}`);
      }
      setResult(json);
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
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
            Rack tiles
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
              {loading ? "Solving..." : "Solve board"}
            </button>
          </div>
          <label className={`${ui.label} mt-6`} htmlFor="board-input">
            Board layout
          </label>
          <textarea
            id="board-input"
            className={`${ui.textarea} mt-2`}
            rows={15}
            value={board}
            onChange={(e) => setBoard(e.target.value)}
          />

          {error && <div className={ui.error}>{error}</div>}
        </div>

        {result && (
          <div className={`mt-6 ${ui.card}`}>
            <div className="text-sm font-semibold text-slate-400">
              Best move suggestion
            </div>
            <pre className="mt-3 whitespace-pre-wrap text-slate-200">
              {JSON.stringify(result, null, 2)}
            </pre>
          </div>
        )}
      </div>
    </div>
  );
}
