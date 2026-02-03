import { Link } from "react-router-dom";
import { ui } from "./ui";

export default function MainPage() {
  return (
    <div className={ui.page}>
      <div className={`${ui.container} relative`}>
        <div className="absolute inset-0 -z-10 rounded-3xl bg-slate-900/30 blur-2xl" />
        <h1 className={ui.h1}>Scrabble Helpers</h1>
        <p className="mt-2 text-slate-300">
          Pick a tool to find playable words, validate a word, or solve a board.
        </p>

        <div className="mt-8 grid gap-4 sm:grid-cols-2">
          <Link className={ui.card} to="/finder">
            <div className={ui.h2}>Word Finder</div>
            <p className="mt-2 text-slate-200">
              Enter your tiles and get ranked word options instantly.
            </p>
          </Link>
          <Link className={ui.card} to="/check">
            <div className={ui.h2}>Word Checker</div>
            <p className="mt-2 text-slate-200">
              Verify a word and see its definition.
            </p>
          </Link>
          <Link className={ui.card} to="/board">
            <div className={ui.h2}>Board Solver</div>
            <p className="mt-2 text-slate-200">
              Solve a Scrabble board with scoring suggestions.
            </p>
          </Link>
        </div>
      </div>
    </div>
  );
}
