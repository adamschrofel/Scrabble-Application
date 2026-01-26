import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

export default function DefinitionPage() {
  const { word } = useParams();
  const [definition, setDefinition] = useState("Loading");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    let cancelled = false;

    async function loadDefinition() {
      setError("");
      setDefinition("Loading");

      try {
        const res = await fetch(`/api/define?word=${encodeURIComponent(word)}`);
        console.log(res.status);

        const json = await res.json();
        console.log(json);

        if (!res.ok || !json.found) {
          return;
        }
        if (!cancelled) setDefinition(json.definition);
      } catch (e) {
        if (!cancelled) setError(e.message);
        console.log(e);
      }
    }

    loadDefinition();
    return () => {
      cancelled = true;
    };
  }, [word]);

  return (
    <div style={{ maxWidth: 600, margin: "10px", padding: 10 }}>
      <button onClick={() => navigate(-1)} style={{ marginBottom: 2 }}>
        Back
      </button>

      <h1 style={{ marginTop: 2 }}>{word}</h1>

      {error ? <p>Error: {error}</p> : <p>{definition}</p>}
    </div>
  );
}
