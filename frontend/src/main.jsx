import React from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import App from './App';
import TicketList from './TicketList';
import TicketDetail from './TicketDetail';
import AskAi from './AskAi';
import './styles.css';

createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<App />}>
          <Route index element={<TicketList />} />
          <Route path="tickets/:publicId" element={<TicketDetail />} />
          <Route path="ask" element={<AskAi />} />
        </Route>
      </Routes>
    </BrowserRouter>
  </React.StrictMode>
);
