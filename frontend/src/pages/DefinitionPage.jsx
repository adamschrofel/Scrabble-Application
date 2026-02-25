import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import PageShell from "../components/PageShell";
import ErrorBanner from "../components/ErrorBanner";
import { getDefinition } from "../api/scrabbleApi";
import { ui } from "../ui";
import PageHeader from "../components/PageHeader";

export default function DefinitionPage() {
  const { word } = useParams();
  const navigate = useNavigate();

  const [definition, setDefinition] = useState("");
  const [found, setFound] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    let cancelled = false;

    async function load() {
      setError("");
      setDefinition("");
      setFound(null);

      try {
        const json = await getDefinition(word);
        if (cancelled) return;

        setFound(!!json?.found);
        setDefinition(json?.definition || "");
      } catch (e) {
        if (!cancelled) setError(e.message || "Failed to load definition.");
      }
    }

    load();
    return () => {
      cancelled = true;
    };
  }, [word]);

  return (
    <PageShell
      title={word}
      subtitle="Definition"
      showNav={false}

      headerRight={
        
        <button className={ui.back} onClick={() => navigate(-1)}>
          ← Back
        </button>
      }
    >
      <div className={`mt-6 ${ui.card}`}>
        <ErrorBanner message={error} />

        {!error ? (
          found === null ? (
            <p className="text-slate-300">Loading...</p>
          ) : found ? (
            <p className="leading-relaxed text-slate-200">{definition}</p>
          ) : (
            <p className="text-slate-300">No definition found.</p>
          )
        ) : null}
      </div>
    </PageShell>
  );
}
