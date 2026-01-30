import { useNavigate, Link } from "react-router-dom";
import { ui } from "./ui";

export default function WordFinderPage({
  tiles,
  setTiles,
  data,
  setData,
  error,
  setError,
}) {
  const navigate = useNavigate();

  async function handleSolve() {
    const trimmed = tiles.trim();
    if (!trimmed) {
      setError("Enter Tiles");
      setData(null);
      return;
    }

    setError("");
    setData(null);

    try {
      const res = await fetch(
        `/api/solve?tiles=${encodeURIComponent(trimmed)}`,
      );
      const json = await res.json();

      if (!res.ok) {
        throw new Error(json?.message || `Failed ${res.status}`);
      }
      setData(json);
    } catch (e) {
      setError(e.message);
    }
  }

  function onKeyDown(e) {
    if (e.key === "Enter") handleSolve();
  }

  return (
    <div className={ui.page}>
      <div className={`${ui.container} relative`}>
        <div className="absolute inset-0 -z-10 rounded-3xl bg-slate-900/30 blur-2xl" />

        <Link to="/" className="inline-block">
          <h1 className={ui.h1}>Scrabble Helpers</h1>
        </Link>
        <p className="mt-2 text-slate-300">
          Choose a tool: Check if a word is valid, 
          solve a board, or simply find words to play by providing your letters!
        </p>

        <div className="mt-4 flex flex-wrap gap-3">
          <Link className={ui.back} to="/check">
            Word checker
          </Link>
          <Link className={ui.back} to="/board">
            Board solver
          </Link>
        </div>

        <div className={`mt-6 ${ui.card}`}>
          <div className="flex gap-3">
            <input
              className={ui.input}
              value={tiles}
              onChange={(e) => setTiles(e.target.value)}
              onKeyDown={onKeyDown}
              placeholder="e.g., triedest (?/* = blank)"
            />
            <button className={ui.button} onClick={handleSolve}>
              Solve
            </button>
          </div>

          {error && <div className={ui.error}>{error}</div>}
        </div>

        {data?.groups && (
          <div className="mt-8 space-y-8">
            {data.groups.length === 0 ? (
              <p className="text-slate-300">No words found!</p>
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
        )}
      </div>
    </div>
  );
}
