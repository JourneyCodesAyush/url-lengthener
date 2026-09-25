import { normalizeUrl } from './normalize.js';
import { assert } from './assert.js';

const API_BASE = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';

export async function getUrl(hash) {
  assert(typeof hash === 'string', 'getUrl expects a string');
  const res = await fetch(`${API_BASE}/api/urls/${hash}`);
  if (res.status === 404) return null;
  if (!res.ok) throw new Error(`Failed to fetch url: ${res.status}`);
  const data = await res.json();
  return data.url;
}

export async function saveUrl(rawUrl) {
  assert(typeof rawUrl === 'string', 'saveUrl expects a string');
  const normalized = normalizeUrl(rawUrl); // throws on genuinely invalid URL — let it propagate as-is

  let res;
  try {
    res = await fetch(`${API_BASE}/api/urls`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ url: normalized }),
    });
  } catch (networkErr) {
    throw new Error('NETWORK_ERROR');
  }

  if (!res.ok) {
    throw new Error('SERVER_ERROR');
  }
  const data = await res.json();
  return data.hash;
}

export async function exportAll() {
  throw new Error('export not yet implemented on the backend');
}

export async function importAll() {
  throw new Error('import not yet implemented on the backend');
}
