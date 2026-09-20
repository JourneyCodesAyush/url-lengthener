import { assert } from './assert';

export function normalizeUrl(rawUrl) {
  assert(typeof rawUrl === 'string', 'normalizeUrl expects a string');
  const url = new URL(rawUrl); // throws on an invalid input
  url.protocol = url.protocol.toLowerCase();
  url.hostname = url.hostname.toLowerCase();
  if (url.pathname.endsWith('/') && url.pathname !== '/') {
    url.pathname = url.pathname.slice(0, -1);
  }
  const result = url.toString();
  assert(result.length > 0, 'normalizeUrl should never produce an empty string');
  return result;
}
