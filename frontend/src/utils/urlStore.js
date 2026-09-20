import { normalizeUrl } from './normalize.js';
import { sha256Hex } from './hash.js';
import { assert } from './assert.js';

const STORAGE_KEY = 'lengthener:urls';

function readAll() {
  const raw = localStorage.getItem(STORAGE_KEY);
  return raw ? JSON.parse(raw) : {};
}

function writeAll(entries) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(entries));
}

export function getUrl(hash) {
  assert(typeof hash === 'string', 'getUrl expects a string');
  const entries = readAll();
  return entries[hash]?.url ?? null;
}

export async function saveUrl(rawUrl) {
  assert(typeof rawUrl === 'string', 'saveUrl expects a string');
  const normalized = normalizeUrl(rawUrl);
  const hash = await sha256Hex(normalized);
  const entries = readAll();
  if (!entries[hash]) {
    entries[hash] = { url: rawUrl, createdAt: new Date().toISOString() };
    writeAll(entries);
  }
  assert(typeof hash === 'string' && hash.length === 64, 'saveUrl must return a valid hash');
  return hash;
}

export function exportAll() {
  const entries = readAll();
  const result = {
    version: 1,
    urls: Object.entries(entries).map(([hash, v]) => ({ hash, ...v })),
  };
  assert(Array.isArray(result.urls), 'exportAll must return an array of urls');
  return result;
}

export async function importAll(data) {
  assert(Array.isArray(data?.urls), 'importAll expects { urls: [...] }');
  const entries = readAll();
  const results = await Promise.all(
    (data.urls ?? []).map(async (item) => {
      const hash = await sha256Hex(normalizeUrl(item.url));
      if (!entries[hash]) {
        entries[hash] = { url: item.url, createdAt: item.createdAt ?? new Date().toISOString() };
        return true;
      }
      return false;
    }),
  );
  writeAll(entries);
  const added = results.filter(Boolean).length;
  assert(typeof added === 'number' && added >= 0, 'importAll must return a valid count');
  return added;
}
