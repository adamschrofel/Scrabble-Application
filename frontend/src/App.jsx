import { Routes, Route, Navigate } from "react-router-dom";
import HomePage from "./pages/HomePage";
import WordFinderPage from "./pages/WordFinderPage";
import DefinitionPage from "./pages/DefinitionPage";
import WordCheckPage from "./pages/WordCheckPage";
import BoardSolverPage from "./pages/BoardSolverPage";

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
      <Route path="/finder" element={<WordFinderPage />} />
      <Route path="/define/:word" element={<DefinitionPage />} />
      <Route path="/check" element={<WordCheckPage />} />
      <Route path="/board" element={<BoardSolverPage />} />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
