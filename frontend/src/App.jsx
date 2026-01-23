import { useState } from 'react'

import './App.css'

export default function App() {
  const [tiles, setTiles] = useState("triedest");
  const [data, setData] = useState(null);
  const [err, setErr] = useState("");

  async function solve() {
    setErr("");
    setData(null);
    try{
      const res = await fetch(`/api/solve?tiles=${encodeURIComponent(tiles)}`);
      if(!res.ok) throw new Error(`HTTP ${res.status}`);
      const json = await res.json();
      setData(json);
    } catch (e){
      setErr(String(e));
    }
  }
  return (
    <div style={{maxWidth: 600, margin: "20px auto", padding: 16}}>
      <h1>Scrabble Word Finder</h1>

      <div style={{display: "flex", gap: 10}}>
        <input
          value={tiles}
          onChange= {(e)=> setTiles(e.target.value)}
          />
        <button onClick={solve} style= {{padding: "10 px 10 px", fontSize: 10}}>
          Solve
        </button>
      </div>

      {err && <p style={{ marginTop: 16 }}>Error: {err}</p>}

      {data && (
        <pre style={{ marginTop: 16, overflowX: "auto" }}>
          {JSON.stringify(data, null, 2)}
        </pre>
      )}

    </div>
  )
}


