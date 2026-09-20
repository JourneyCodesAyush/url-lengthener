import { describe, it, expect } from 'vitest';
import { sha256Hex } from '../../src/utils/hash';

describe('sha256Hex', () => {
  it('is deterministic for the same input', async () => {
    const a = await sha256Hex('https://example.com');
    const b = await sha256Hex('https://example.com');
    expect(a).toBe(b);
  });

  it('produces different hashes for different input', async () => {
    const a = await sha256Hex('https://example.com/a');
    const b = await sha256Hex('https://example.com/b');
    expect(a).not.toBe(b);
  });

  it('returns a 64-character hex string', async () => {
    const hash = await sha256Hex('https://example.com');
    expect(hash).toMatch(/^[0-9a-f]{64}$/);
  });
});
