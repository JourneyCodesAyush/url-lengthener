import { describe, it, expect, beforeEach } from 'vitest';
import { getUrl, saveUrl, exportAll, importAll } from '../../src/utils/urlStore';

beforeEach(() => {
  localStorage.clear();
});

describe('saveUrl / getUrl', () => {
  it('round-trips a saved url', async () => {
    const hash = await saveUrl('https://example.com');
    expect(getUrl(hash)).toBe('https://example.com');
  });

  it('returns null for an unknown hash', () => {
    expect(getUrl('nonexistent')).toBeNull();
  });

  it('is idempotent: saving the same url twice yields the same hash', async () => {
    const hashA = await saveUrl('https://example.com');
    const hashB = await saveUrl('https://example.com');
    expect(hashA).toBe(hashB);
  });

  it('treats normalized-equivalent urls as duplicates', async () => {
    const hashA = await saveUrl('https://example.com/path/');
    const hashB = await saveUrl('https://EXAMPLE.com/path');
    expect(hashA).toBe(hashB);
  });
});

describe('export / import round trip', () => {
  it('reproduces the same store after export then import into a fresh store', async () => {
    await saveUrl('https://example.com/a');
    await saveUrl('https://example.com/b');

    const exported = exportAll();
    localStorage.clear();

    const added = await importAll(exported);
    expect(added).toBe(2);

    const reimported = exportAll();
    const urlsA = exported.urls.map((e) => e.url).sort();
    const urlsB = reimported.urls.map((e) => e.url).sort();
    expect(urlsB).toEqual(urlsA);
  });
});
