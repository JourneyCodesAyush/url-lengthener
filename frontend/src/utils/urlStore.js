const mode = import.meta.env.VITE_STORAGE_MODE ?? 'local';

const impl =
  mode === 'api' ? await import('./urlStore.api.js') : await import('./urlStore.local.js');

export const { getUrl, saveUrl, exportAll, importAll } = impl;
