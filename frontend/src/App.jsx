import { Link, Outlet } from 'react-router-dom';

export default function App() {
  return (
    <div className="layout">
      <header>
        <h1>Support Tickets</h1>
        <nav>
          <Link to="/">Tickets</Link>
          <Link to="/ask">Ask AI</Link>
        </nav>
      </header>
      <main>
        <Outlet />
      </main>
    </div>
  );
}
