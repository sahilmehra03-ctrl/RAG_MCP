# RAG ingestion

## Triggers

- After ticket created.
- After ticket updated (title, description, priority, assignee, category, resolution notes).
- After comment added.
- After status transition (especially RESOLVED/CLOSED).

## Pipeline

1. Build canonical text from ticket + comments.
2. Delete existing vectors for `ticketId`.
3. Split into chunks (see `architecture.md`).
4. Attach metadata per chunk.
5. `vectorStore.add(documents)`.

## Stale data

Re-ingest replaces all chunks for a ticket; no partial updates without delete-first.
