import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { getUrl } from '../utils/urlStore.js';

export default function RedirectHandler() {
  const { hash } = useParams();
  const [url, setUrl] = useState(undefined); // undefined = loading, null = not found

  useEffect(() => {
    let cancelled = false;
    getUrl(hash).then((result) => {
      if (!cancelled) setUrl(result);
    });
    return () => {
      cancelled = true;
    };
  }, [hash]);

  useEffect(() => {
    if (url) {
      window.location.href = url;
    }
  }, [url]);

  if (url === undefined) return null; // still loading
  if (url) return null; // redirecting
  return (
    <div className="shell">
      <p className="title error">nothing lengthened here yet</p>
      <p className="muted">this hash does not match anything in storage.</p>
    </div>
  );
}
