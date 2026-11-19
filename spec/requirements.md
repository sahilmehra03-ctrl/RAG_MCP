# Requirements

## Functional

### Tickets

- Create, list, view, update tickets (title, description, priority, assignee).
- Comments on tickets.
- Keyword search and filter by status.
- Persist to database; survive restart.
- Backend validation; errors surfaced in UI.

### State machine (backend enforced)

| From | Allowed to |
|------|------------|
| OPEN | IN_PROGRESS, CANCELLED |
| IN_PROGRESS | RESOLVED, CANCELLED |
| RESOLVED | CLOSED |
| CLOSED | (terminal) |
| CANCELLED | (terminal) |

Reject: RESOLVED→OPEN, CLOSED→OPEN, CANCELLED→OPEN, and any other invalid transition.

### RAG

- Ingest ticket knowledge: description, comments, resolution notes.
- Chunk, embed, store with metadata: `ticketId`, `status`, `priority`, `assignee`, `category`.
- Re-ingest on ticket update and close.
- `POST /api/ai/ask` with `{ "question": "..." }`.
- Single retrieve → generate (not an agent).
- Answer only from retrieved chunks; cite ticket ID(s).
- No match → explicit message, no fabrication.
- Configurable `top-K` and similarity threshold.

## Non-functional

- Java 21, Spring Boot 3.x, Maven.
- Dev: H2; optional PostgreSQL + PGVector via Docker.
- LLM/embeddings: Ollama (env-configurable); cloud alternatives documented in architecture.
- No secrets in git.

## Acceptance checklist

- [ ] CRUD UI, search/filter
- [ ] Valid/invalid transitions + integration tests
- [ ] Persistence after restart
- [ ] Validation/errors in UI
- [ ] Embeddings + vector store
- [ ] Grounded `/api/ai/ask` with citations
- [ ] Honest no-match responses
- [ ] Re-ingestion on update
- [ ] Documented chunking/model
- [ ] Configurable retrieval
- [ ] Documented AI mistake in `docs/prompt-history.md`
