import { assert } from './assert';

export async function sha256Hex(text) {
  assert(typeof text === 'string', 'sha256Hex expects a string');
  const data = new TextEncoder().encode(text);
  const digest = await crypto.subtle.digest('SHA-256', data);
  const hex = [...new Uint8Array(digest)].map((b) => b.toString(16).padStart(2, '0')).join('');
  assert(hex.length === 64, 'sha256 digest should always be 64 hex chars');
  return hex;
}
