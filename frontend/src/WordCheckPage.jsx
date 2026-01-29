import { useState } from "react";
import { Link } from "react-router-dom";
import { ui } from "./ui";

export default function WordCheckPage() {
  const [word, setWord] = useState("");
  const [result, setResult] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  async function handleCheck() {
    const trimmed = word.trim();
    if (!trimmed) {
      setError("Enter a word to check.");
      setResult(null);
      return;
    }

    setError("");
    setLoading(true);
    setResult(null);

    try {
      const res = await fetch(`/api/define?word=${encodeURIComponent(trimmed)}`);
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

  function onKeyDown(e) {
    if (e.key === "Enter") handleCheck();
  }

  return (
    <div className={ui.page}>
      <div className={ui.container}>
        <Link className={ui.back} to="/">
          ← Back
        </Link>

        <h1 className={`mt-6 ${ui.h1}`}>Word Check</h1>
        <p className="mt-2 text-slate-300">
          Enter a word to verify it exists and see its definition.
        </p>

        <div className={`mt-6 ${ui.card}`}>
          <div className="flex gap-3">
            <input
              className={ui.input}
              value={word}
              onChange={(e) => setWord(e.target.value)}
              onKeyDown={onKeyDown}
              placeholder="e.g., quixotic"
            />
            <button
              className={ui.button}
              onClick={handleCheck}
              disabled={loading}
            >
              {loading ? "Checking..." : "Check"}
            </button>
          </div>

          {error && <div className={ui.error}>{error}</div>}
        </div>

        {result && (
          <div className={`mt-6 ${ui.card}`}>
            <div className="text-sm font-semibold text-slate-400">
              {result.word}
            </div>
            {result.found ? (
              <p className="mt-3 text-slate-200">{result.definition}</p>
            ) : (
              <p className="mt-3 text-slate-300">
                Not found in the dictionary.
              </p>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
