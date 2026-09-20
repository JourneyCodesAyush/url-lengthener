import { describe, it, expect } from 'vitest';
import { normalizeUrl } from '../../src/utils/normalize';

describe('normalizeUrl', () => {
  it('lowercases scheme and host', () => {
    expect(normalizeUrl('HTTPS://Example.COM/path')).toBe('https://example.com/path');
  });

  it('strips a single trailing slash', () => {
    expect(normalizeUrl('https://example.com/path/')).toBe('https://example.com/path');
  });

  it('does not strip the root slash', () => {
    expect(normalizeUrl('https://example.com/')).toBe('https://example.com/');
  });

  it('leaves query strings untouched', () => {
    expect(normalizeUrl('https://example.com/path?ref=x')).toBe('https://example.com/path?ref=x');
  });

  it('treats different query strings as distinct', () => {
    const a = normalizeUrl('https://example.com/path?ref=x');
    const b = normalizeUrl('https://example.com/path?ref=y');
    expect(a).not.toBe(b);
  });

  it('throws on invalid input', () => {
    expect(() => normalizeUrl('not a url')).toThrow();
  });
});
