/**
 * Minimal fetch wrapper:
 * - Parses JSON when possible
 * - Throws on non-2xx with a useful message
 */
export async function apiJson(url, options = {}) {
  const headers = {
    ...(options.body ? { "Content-Type": "application/json" } : {}),
    ...(options.headers ?? {}),
  };

  const res = await fetch(url, { ...options, headers });

  // Read as text first so we can handle empty bodies / non-JSON errors gracefully.
  const raw = await res.text();
  let json = null;
  if (raw) {
    try {
      json = JSON.parse(raw);
    } catch {
      // ignore: not JSON
    }
  }

  if (!res.ok) {
    const message =
      (json && (json.message || json.error || json.detail)) ||
      `Request failed (${res.status})`;
    const err = new Error(message);
    err.status = res.status;
    err.body = json ?? raw;
    throw err;
  }

  return json;
}
