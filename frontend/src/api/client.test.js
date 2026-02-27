import test from "node:test";
import assert from "node:assert/strict";
import { apiJson } from "./client.js";

test("apiJson throws with json.message on non-2xx", async () => {
  const originalFetch = globalThis.fetch;
  globalThis.fetch = async () => ({
    ok: false,
    status: 400,
    text: async () => JSON.stringify({ message: "Bad input" }),
  });

  try {
    await assert.rejects(() => apiJson("/api/test"), (err) => {
      assert.equal(err.message, "Bad input");
      assert.equal(err.status, 400);
      return true;
    });
  } finally {
    globalThis.fetch = originalFetch;
  }
});

test("apiJson falls back to status message when body is not json", async () => {
  const originalFetch = globalThis.fetch;
  globalThis.fetch = async () => ({
    ok: false,
    status: 500,
    text: async () => "<html>error</html>",
  });

  try {
    await assert.rejects(() => apiJson("/api/test"), (err) => {
      assert.equal(err.message, "Request failed (500)");
      assert.equal(err.status, 500);
      return true;
    });
  } finally {
    globalThis.fetch = originalFetch;
  }
});

test("apiJson returns parsed json on success", async () => {
  const originalFetch = globalThis.fetch;
  globalThis.fetch = async () => ({
    ok: true,
    status: 200,
    text: async () => JSON.stringify({ ok: true }),
  });

  try {
    const res = await apiJson("/api/test");
    assert.deepEqual(res, { ok: true });
  } finally {
    globalThis.fetch = originalFetch;
  }
});
