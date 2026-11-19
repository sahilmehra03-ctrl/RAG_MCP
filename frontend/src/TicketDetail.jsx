import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { api } from './api';

const NEXT = {
  OPEN: ['IN_PROGRESS', 'CANCELLED'],
  IN_PROGRESS: ['RESOLVED', 'CANCELLED'],
  RESOLVED: ['CLOSED'],
};

export default function TicketDetail() {
  const { publicId } = useParams();
  const [ticket, setTicket] = useState(null);
  const [error, setError] = useState(null);
  const [edit, setEdit] = useState({});
  const [comment, setComment] = useState({ author: '', body: '' });

  const load = () => api.getTicket(publicId).then(setTicket).catch((e) => setError(e.message));

  useEffect(() => {
    load();
  }, [publicId]);

  useEffect(() => {
    if (ticket) {
      setEdit({
        title: ticket.title,
        description: ticket.description,
        priority: ticket.priority,
        assignee: ticket.assignee || '',
        category: ticket.category || '',
        resolutionNotes: ticket.resolutionNotes || '',
      });
    }
  }, [ticket]);

  const save = async () => {
    setError(null);
    try {
      await api.updateTicket(publicId, edit);
      load();
    } catch (err) {
      setError(err.body?.message || err.message);
    }
  };

  const doTransition = async (status) => {
    setError(null);
    try {
      await api.transition(publicId, status);
      load();
    } catch (err) {
      setError(err.body?.message || err.message);
    }
  };

  const submitComment = async (e) => {
    e.preventDefault();
    setError(null);
    try {
      await api.addComment(publicId, comment);
      setComment({ author: '', body: '' });
      load();
    } catch (err) {
      setError(err.body?.message || err.message);
    }
  };

  if (!ticket) return <p>Loading…</p>;

  return (
    <div className="card">
      <h2>{ticket.publicId}</h2>
      {error && <p className="error">{error}</p>}
      <div className="form-grid">
        <input value={edit.title} onChange={(e) => setEdit({ ...edit, title: e.target.value })} />
        <textarea
          value={edit.description}
          onChange={(e) => setEdit({ ...edit, description: e.target.value })}
        />
        <select
          value={edit.priority}
          onChange={(e) => setEdit({ ...edit, priority: e.target.value })}
        >
          {['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'].map((p) => (
            <option key={p} value={p}>{p}</option>
          ))}
        </select>
        <input
          placeholder="Assignee"
          value={edit.assignee}
          onChange={(e) => setEdit({ ...edit, assignee: e.target.value })}
        />
        <input
          placeholder="Category"
          value={edit.category}
          onChange={(e) => setEdit({ ...edit, category: e.target.value })}
        />
        <textarea
          placeholder="Resolution notes"
          value={edit.resolutionNotes}
          onChange={(e) => setEdit({ ...edit, resolutionNotes: e.target.value })}
        />
        <button type="button" onClick={save}>Save</button>
      </div>

      <p>Status: <strong>{ticket.status}</strong></p>
      <div className="actions">
        {(NEXT[ticket.status] || []).map((s) => (
          <button key={s} type="button" onClick={() => doTransition(s)}>→ {s}</button>
        ))}
      </div>

      <h3>Comments</h3>
      <ul>
        {ticket.comments.map((c) => (
          <li key={c.id}><strong>{c.author}</strong>: {c.body}</li>
        ))}
      </ul>
      <form onSubmit={submitComment} className="form-grid">
        <input
          placeholder="Author"
          value={comment.author}
          onChange={(e) => setComment({ ...comment, author: e.target.value })}
          required
        />
        <textarea
          placeholder="Comment"
          value={comment.body}
          onChange={(e) => setComment({ ...comment, body: e.target.value })}
          required
        />
        <button type="submit">Add comment</button>
      </form>
    </div>
  );
}
