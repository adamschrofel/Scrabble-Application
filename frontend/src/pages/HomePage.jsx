import { Link } from "react-router-dom";
import PageShell from "../components/PageShell";
import { ui } from "../ui";

export default function HomePage() {
  return (
    <PageShell
      title="Scrabble Helpers"
      subtitle="Pick a tool to find playable words, validate a word, or solve a board."
      showNav={false}
    >
      {/* Info Section */}
      <div className="mt-6 max-w-2xl text-slate-300 text-sm leading-relaxed">
        <p>
          This project is a full-stack Scrabble analysis tool that uses a
          dictionary-backed solver to generate valid words, validate entries,
          and evaluate board placements with scoring.
        </p>

        <p className="mt-3">
          Note: The backend is hosted on a free server. If it has been idle,
          the first request may take <span className="font-medium">20–what feels like forever seconds</span> while
          the server spins up. Subsequent requests will respond normally.
        </p>
      </div>

      {/* Tool Cards */}
      <div className="mt-8 grid gap-4 sm:grid-cols-2">
        <Link className={ui.card} to="/finder">
          <div className={ui.h2}>Word Finder</div>
          <p className="mt-2 text-slate-200">
            Enter your tiles and generate valid playable words ranked by score.
          </p>
        </Link>

        <Link className={ui.card} to="/check">
          <div className={ui.h2}>Word Checker</div>
          <p className="mt-2 text-slate-200">
            Validate a word against the dictionary and view its definition.
          </p>
        </Link>

        <Link className={ui.card} to="/board">
          <div className={ui.h2}>Board Solver</div>
          <p className="mt-2 text-slate-200">
            Analyze a board state and receive scoring suggestions for possible
            plays.
          </p>
        </Link>
      </div>
    </PageShell>
  );
}