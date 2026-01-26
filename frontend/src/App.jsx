import { useState } from "react";
import { Routes, Route, Navigate } from "react-router-dom";
import WordFinderPage from "./WordFinderPage.jsx";
import DefinitionPage from "./DefinitionPage.jsx";

export default function App() {
  const [tiles, setTiles] = useState("");
  const [data, setData] = useState(null);
  const [error, setError] = useState("");
  return (
    <Routes>
      <Route
        path="/"
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
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
