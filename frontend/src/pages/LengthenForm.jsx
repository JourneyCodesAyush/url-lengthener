import { useState } from 'react';
import { saveUrl } from '../utils/urlStore.js';
import ImportExport from '../components/ImportExport.jsx';

export default function LengthenForm() {
  const [input, setInput] = useState('');
  const [hash, setHash] = useState(null);
  const [copied, setCopied] = useState(false);
  const [error, setError] = useState(null);

  async function handleSubmit(e) {
    e.preventDefault();
    setError(null);
    try {
      const result = await saveUrl(input);
      setHash(result);
      setCopied(false);
    } catch {
      setError('That does not look like a valid url.');
    }
  }

  const lengthenedUrl = hash ? `${window.location.origin}/url/${hash}` : null;

  async function handleCopy() {
    await navigator.clipboard.writeText(lengthenedUrl);
    setCopied(true);
    setTimeout(() => setCopied(false), 1500);
  }

  return (
    <div className="shell">
      <p className="title">url // lengthener</p>
      <form onSubmit={handleSubmit}>
        <input
          type="text"
          placeholder="paste a url"
          value={input}
          onChange={(e) => setInput(e.target.value)}
        />
        <button type="submit">lengthen</button>
      </form>
      {error && <p className="error">{error}</p>}
      {lengthenedUrl && (
        <div className="result">
          <p className="hash">{lengthenedUrl}</p>
          <button type="button" onClick={handleCopy}>
            {copied ? 'copied' : 'copy'}
          </button>
        </div>
      )}
      <ImportExport />
    </div>
  );
}
