const API_BASE = (import.meta.env?.VITE_API_BASE_URL ?? "").toString();

function withBase(url) {
  // Allow absolute URLs as-is.
  if (/^https?:\/\//i.test(url)) return url;

  // Default: relative to the current origin (works with Vite proxy in dev).
  if (!API_BASE) return url;

  // Join API_BASE + url.
  const base = API_BASE.endsWith("/") ? API_BASE.slice(0, -1) : API_BASE;
  return `${base}${url.startsWith("/") ? url : `/${url}`}`;
}
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

  // Read as text first to handle empty bodies / non-JSON errors gracefully.
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
