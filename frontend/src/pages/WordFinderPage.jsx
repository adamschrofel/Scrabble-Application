import { useState } from "react";
import { useNavigate } from "react-router-dom";
import PageShell from "../components/PageShell";
import ErrorBanner from "../components/ErrorBanner";
import { solveRack } from "../api/scrabbleApi";
import { ui } from "../ui";

export default function WordFinderPage() {
  const [tiles, setTiles] = useState("");
  const [data, setData] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();

  async function handleSolve() {
    const rack = tiles.trim();
    if (!rack) {
      setError("Enter tiles.");
      setData(null);
      return;
    }

    setError("");
    setData(null);
    setLoading(true);

    try {
      const json = await solveRack(rack);
      setData(json);
    } catch (e) {
      setError(e.message || "Solve failed.");
    } finally {
      setLoading(false);
    }
  }

  function onKeyDown(e) {
    if (e.key === "Enter") handleSolve();
  }

  return (
    <PageShell
      title="Scrabble Helpers"
      subtitle="Enter your rack and get ranked word options."
      
    >
      <div className={`mt-6 ${ui.card}`}>
        <div className="flex gap-3">
          <input
            className={ui.input}
            value={tiles}
            onChange={(e) => setTiles(e.target.value)}
            onKeyDown={onKeyDown}
            placeholder="e.g., triedest (?/* = blank)"
          />
          <button
            className={ui.button}
            onClick={handleSolve}
            disabled={loading}
          >
            {loading ? "Solving..." : "Solve"}
          </button>
        </div>

        <ErrorBanner message={error} />
      </div>

      {data?.groups ? (
        <div className="mt-8 space-y-8">
          {data.groups.length === 0 ? (
            <p className="text-slate-300">No words found.</p>
          ) : (
            data.groups.map((g) => (
              <section key={g.length}>
                <div className="mb-3 text-sm font-semibold text-slate-400">
                  {g.length}-letter words
                </div>

                <div className="grid grid-cols-2 gap-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5">
                  {g.words.map((w) => (
                    <button
                      key={w.word}
                      onClick={() => navigate(`/define/${w.word}`)}
                      className={ui.chip}
                      title={`Score: ${w.score}`}
                    >
                      <div className="flex items-center justify-between">
                        <span>{w.word}</span>
                        <span className="ml-2 text-sm text-slate-400">
                          {w.score}
                        </span>
                      </div>
                    </button>
                  ))}
                </div>
              </section>
            ))
          )}
        </div>
      ) : null}
    </PageShell>
  );
}
