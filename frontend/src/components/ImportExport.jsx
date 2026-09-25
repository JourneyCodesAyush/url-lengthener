import { useRef, useState } from 'react';
import { exportAll, importAll } from '../utils/urlStore.js';

export default function ImportExport() {
  const fileInput = useRef(null);
  const [error, setError] = useState(null);

  async function handleExport() {
    setError(null);
    try {
      const data = await exportAll();
      const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' });
      const link = document.createElement('a');
      link.href = URL.createObjectURL(blob);
      link.download = 'lengthener-export.json';
      link.click();
    } catch {
      setError('could not export.');
    }
  }

  async function handleImport(e) {
    const file = e.target.files[0];
    if (!file) return;
    setError(null);
    const text = await file.text();
    try {
      await importAll(JSON.parse(text));
    } catch {
      setError('could not read that file.');
    }
  }

  return (
    <div className="import-export">
      <button type="button" onClick={handleExport}>
        export
      </button>
      <button type="button" onClick={() => fileInput.current.click()}>
        import
      </button>
      <input type="file" accept=".json" ref={fileInput} onChange={handleImport} hidden />
      {error && <p className="error">{error}</p>}
    </div>
  );
}
