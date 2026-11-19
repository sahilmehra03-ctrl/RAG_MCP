import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api } from './api';

export default function TicketList() {
  const [tickets, setTickets] = useState([]);
  const [status, setStatus] = useState('');
  const [q, setQ] = useState('');
  const [form, setForm] = useState({ title: '', description: '', priority: 'MEDIUM' });
  const [error, setError] = useState(null);

  const load = () => {
    api.listTickets(status || undefined, q || undefined).then(setTickets).catch((e) => setError(e.message));
  };

  useEffect(() => {
    load();
  }, [status, q]);

  const create = async (e) => {
    e.preventDefault();
    setError(null);
    try {
      await api.createTicket(form);
      setForm({ title: '', description: '', priority: 'MEDIUM' });
      load();
    } catch (err) {
      setError(err.body?.message || err.message);
    }
  };

  return (
    <div>
      <section className="card">
        <h2>Create ticket</h2>
        {error && <p className="error">{error}</p>}
        <form onSubmit={create} className="form-grid">
          <input
            placeholder="Title"
            value={form.title}
            onChange={(e) => setForm({ ...form, title: e.target.value })}
            required
          />
          <textarea
            placeholder="Description"
            value={form.description}
            onChange={(e) => setForm({ ...form, description: e.target.value })}
            required
          />
          <select
            value={form.priority}
            onChange={(e) => setForm({ ...form, priority: e.target.value })}
          >
            {['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'].map((p) => (
              <option key={p} value={p}>{p}</option>
            ))}
          </select>
          <button type="submit">Create</button>
        </form>
      </section>

      <section className="card">
        <h2>Tickets</h2>
        <div className="filters">
          <input placeholder="Search" value={q} onChange={(e) => setQ(e.target.value)} />
          <select value={status} onChange={(e) => setStatus(e.target.value)}>
            <option value="">All statuses</option>
            {['OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED', 'CANCELLED'].map((s) => (
              <option key={s} value={s}>{s}</option>
            ))}
          </select>
        </div>
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Title</th>
              <th>Status</th>
              <th>Priority</th>
            </tr>
          </thead>
          <tbody>
            {tickets.map((t) => (
              <tr key={t.publicId}>
                <td><Link to={`/tickets/${t.publicId}`}>{t.publicId}</Link></td>
                <td>{t.title}</td>
                <td>{t.status}</td>
                <td>{t.priority}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </section>
    </div>
  );
}
