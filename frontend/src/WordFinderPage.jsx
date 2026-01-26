import { useNavigate } from "react-router-dom";
import "./App.css";

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
    <div style={{ maxWidth: 600, margin: "20px auto", padding: 16 }}>
      <h1>Scrabble Word Finder</h1>

      <div style={{ display: "flex", gap: 10 }}>
        <input
          value={tiles}
          onChange={(e) => setTiles(e.target.value)}
          onKeyDown={onKeyDown}
          placeholder="e.g., aeioust"
          style={{ flex: 1, padding: 10 }}
        />
        <button
          onClick={handleSolve}
          style={{ padding: "10px 10px", fontSize: 10 }}
        >
          Solve
        </button>
      </div>

      {error && <p style={{ marginTop: 16 }}>Error: {error}</p>}

      {data?.groups && (
        <div style={{ marginTop: 16 }}>
          {data.groups.length === 0 ? (
            <p>No words found.</p>
          ) : (
            data.groups.map((g) => (
              <div key={g.length} style={{ marginBottom: 16 }}>
                <h2 style={{ margin: "10px 0" }}>{g.length}-letter words</h2>
                <div
                  style={{
                    display: "grid",
                    gridTemplateColumns:
                      "repeat(auto-fill, minmax(120px, 1fr))",
                    gap: 6,
                  }}
                >
                  {g.words.map((w) => (
                    <div
                      key={w}
                      onClick={() => navigate(`/define/${w}`)}
                      style={{
                        padding: "6px 8px",
                        border: "1px solid #ddd",
                        borderRadius: 8,
                        cursor: "pointer",
                        textAlign: "center",
                        background: "#eef",
                        color: "black",
                      }}
                    >
                      {w}
                    </div>
                  ))}
                </div>
              </div>
            ))
          )}
        </div>
      )}
    </div>
  );
}
