import { BrowserRouter, Routes, Route } from 'react-router-dom';
import LengthenForm from './pages/LengthenForm';
import RedirectHandler from './pages/RedirectHandler';

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<LengthenForm />} />
        <Route path="/url/:hash" element={<RedirectHandler />} />
      </Routes>
    </BrowserRouter>
  );
}
