import { useState } from 'react';
import { Link } from 'react-router-dom';
import { api } from './api';

export default function AskAi() {
  const [question, setQuestion] = useState('');
  const [response, setResponse] = useState(null);
  const [error, setError] = useState(null);

  const submit = async (e) => {
    e.preventDefault();
    setError(null);
    setResponse(null);
    try {
      const res = await api.ask(question);
      setResponse(res);
    } catch (err) {
      setError(err.body?.message || err.message);
    }
  };

  return (
    <div className="card">
      <h2>Ask AI</h2>
      <form onSubmit={submit} className="form-grid">
        <textarea
          placeholder="e.g. What caused payment failures?"
          value={question}
          onChange={(e) => setQuestion(e.target.value)}
          required
          minLength={3}
        />
        <button type="submit">Ask</button>
      </form>
      {error && <p className="error">{error}</p>}
      {response && (
        <div className="answer">
          <p>{response.answer}</p>
          {response.citedTicketIds?.length > 0 && (
            <p>
              Citations:{' '}
              {response.citedTicketIds.map((id) => (
                <Link key={id} to={`/tickets/${id}`}>{id} </Link>
              ))}
            </p>
          )}
          <p className="muted">Grounded: {response.grounded ? 'yes' : 'no'}</p>
        </div>
      )}
    </div>
  );
}
