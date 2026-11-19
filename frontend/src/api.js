const API_BASE = import.meta.env.VITE_API_BASE || 'http://localhost:8080/api';

async function request(path, options = {}) {
  const res = await fetch(`${API_BASE}${path}`, {
    headers: { 'Content-Type': 'application/json', ...options.headers },
    ...options,
  });
  const body = await res.json().catch(() => ({}));
  if (!res.ok) {
    const err = new Error(body.message || res.statusText);
    err.status = res.status;
    err.body = body;
    throw err;
  }
  return body;
}

export const api = {
  listTickets: (status, q) => {
    const params = new URLSearchParams();
    if (status) params.set('status', status);
    if (q) params.set('q', q);
    const qs = params.toString();
    return request(`/tickets${qs ? `?${qs}` : ''}`);
  },
  createTicket: (data) => request('/tickets', { method: 'POST', body: JSON.stringify(data) }),
  getTicket: (publicId) => request(`/tickets/${publicId}`),
  updateTicket: (publicId, data) =>
    request(`/tickets/${publicId}`, { method: 'PATCH', body: JSON.stringify(data) }),
  transition: (publicId, status) =>
    request(`/tickets/${publicId}/transition`, {
      method: 'POST',
      body: JSON.stringify({ status }),
    }),
  addComment: (publicId, data) =>
    request(`/tickets/${publicId}/comments`, { method: 'POST', body: JSON.stringify(data) }),
  ask: (question) => request('/ai/ask', { method: 'POST', body: JSON.stringify({ question }) }),
};
