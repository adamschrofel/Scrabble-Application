import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { ui } from "./ui";

export default function DefinitionPage() {
  const { word } = useParams();
  const [definition, setDefinition] = useState("Loading...");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    let cancelled = false;

    async function loadDefinition() {
      setError("");
      setDefinition("Loading...");

      try {
        const res = await fetch(`/api/define?word=${encodeURIComponent(word)}`);

        const json = await res.json();

        if (!res.ok || !json.found) {
          if(!cancelled) setDefinition("No definition found");
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
    <div className={ui.page}>
      <div className={ui.container}>
        <button className={ui.back} onClick={() => navigate(-1)}>
          ← Back
        </button>

        <h1 className={`mt-6 ${ui.h1}`}>{word}</h1>

        <div className={`mt-6 ${ui.card}`}>
          {error ? (
            <div className={ui.error}>{error}</div>
          ) : (
            <p className="leading-relaxed text-slate-200">{definition}</p>
          )}
        </div>
      </div>
    </div>
  );
}
