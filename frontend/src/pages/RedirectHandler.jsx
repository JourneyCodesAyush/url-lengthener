import { useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { getUrl } from '../utils/urlStore.js';

export default function RedirectHandler() {
  const { hash } = useParams();
  const url = getUrl(hash);

  useEffect(() => {
    if (url) {
      window.location.href = url;
    }
  }, [url]);

  if (url) return null;

  return (
    <div className="shell">
      <p className="title error">nothing lengthened here yet</p>
      <p className="muted">this hash does not match anything in storage.</p>
    </div>
  );
}
