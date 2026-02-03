import { useState } from "react";
import { Routes, Route, Navigate } from "react-router-dom";
import MainPage from "./MainPage.jsx";
import WordFinderPage from "./WordFinderPage.jsx";
import DefinitionPage from "./DefinitionPage.jsx";
import WordCheckPage from "./WordCheckPage.jsx";
import BoardSolverPage from "./BoardSolverPage.jsx";

export default function App() {
  const [tiles, setTiles] = useState("");
  const [data, setData] = useState(null);
  const [error, setError] = useState("");
  return (
    <Routes>
      <Route path="/" element={<MainPage />} />
      <Route
        path="/finder"
        element={
          <WordFinderPage
            tiles={tiles}
            setTiles={setTiles}
            data={data}
            setData={setData}
            error={error}
            setError={setError}
          />
        }
      />
      <Route path="/define/:word" element={<DefinitionPage />} />
      <Route path="/check" element={<WordCheckPage />} />
      <Route path="/board" element={<BoardSolverPage />} />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
