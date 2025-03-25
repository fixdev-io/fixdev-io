
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import FixDevLanding from "./FixDevLanding";

const App = () => {
  return (
    <BrowserRouter>
      <Routes>
        {/* Redirect from root to /ru by default */}
        <Route path="/" element={<Navigate to="/ru" replace />} />
        
        {/* Language-specific route */}
        <Route path="/:lang" element={<FixDevLanding />} />
      </Routes>
    </BrowserRouter>
  );
};

export default App;
