import { apiJson } from "./client";

export function solveRack(rack) {
  return apiJson(`/api/rack/solve?rack=${encodeURIComponent(rack)}`);
}

export function getDefinition(word) {
  return apiJson(`/api/words/${encodeURIComponent(word)}`);
}

export function getBoard() {
  return apiJson("/api/board");
}

export function resetBoard() {
  return apiJson("/api/board/reset", { method: "POST" });
}

export function setBoardTiles(tiles) {
  return apiJson("/api/board/tiles", {
    method: "POST",
    body: JSON.stringify({ tiles }),
  });
}

export function solveBoard({ rack, limit }) {
  return apiJson("/api/board/solve", {
    method: "POST",
    body: JSON.stringify({ rack, limit }),
  });
}
