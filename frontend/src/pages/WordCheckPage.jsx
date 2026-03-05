import { useState } from "react";
import PageShell from "../components/PageShell";
import ErrorBanner from "../components/ErrorBanner";
import { getDefinition } from "../api/scrabbleApi";
import { ui } from "../ui";

export default function WordCheckPage() {
  const [word, setWord] = useState("");
  const [result, setResult] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  async function handleCheck() {
    const w = word.trim();
    if (!w) {
      setError("Enter a word to check.");
      setResult(null);
      return;
    }

    setError("");
    setResult(null);
    setLoading(true);

    try {
      const json = await getDefinition(w);
      setResult(json);
    } catch (e) {
      setError(e.message || "Check failed.");
    } finally {
      setLoading(false);
    }
  }

  function onKeyDown(e) {
    if (e.key === "Enter") handleCheck();
  }

  return (
    <PageShell title="Word Check" subtitle="Verify a word and see its definition.">
      <div className={`mt-6 ${ui.card}`}>
        <label htmlFor="word-input" className={ui.label}>
          Word to check
        </label>

        <div className="mt-2 flex flex-col gap-3 sm:flex-row">
          <input
            id="word-input"
            className={ui.input}
            value={word}
            onChange={(e) => setWord(e.target.value)}
            onKeyDown={onKeyDown}
            placeholder="e.g., quixotic"
          />
          <button className={ui.button} onClick={handleCheck} disabled={loading}>
            {loading ? "Checking..." : "Check"}
          </button>
        </div>

        <ErrorBanner message={error} />
      </div>

      <div className="sr-only" aria-live="polite">
        {loading ? "Checking word" : result ? `${result.word} ${result.found ? "found" : "not found"}` : ""}
      </div>

      {result ? (
        <div className={`mt-6 ${ui.card}`}>
          <div className="text-sm font-semibold text-slate-400">{result.word}</div>
          {result.found ? (
            <p className="mt-3 text-slate-200">{result.definition}</p>
          ) : (
            <p className="mt-3 text-slate-300">Not found in the dictionary.</p>
          )}
        </div>
      ) : null}
    </PageShell>
  );
}
