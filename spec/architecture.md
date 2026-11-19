# Architecture

## Overview

```
┌─────────────┐     REST      ┌──────────────────┐
│  React UI   │ ────────────► │ Spring Boot API  │
└─────────────┘               │  tickets / RAG   │
                              └────────┬─────────┘
                                       │
                    ┌──────────────────┼──────────────────┐
                    ▼                  ▼                  ▼
              Relational DB      Vector store         Ollama (or cloud)
              (H2 / Postgres)   (in-memory / PGVector)  chat + embeddings
```

## Layers

- **API:** REST controllers, DTO validation, problem details for errors.
- **Domain:** Ticket aggregate, status state machine, comments.
- **Application:** Ticket service, transition guard, RAG ingestion orchestration.
- **Infrastructure:** JPA repositories, Spring AI `VectorStore`, embedding + chat clients.

## Chunking strategy

- **Unit:** One document per ticket at ingest time, split into overlapping text chunks.
- **Algorithm:** Spring AI `TokenTextSplitter` with `chunkSize=512`, `chunkOverlap=50` (tokens, model tokenizer–aware when using Ollama embedding model).
- **Content template per chunk:** `[TKT-{id}] status={status} priority={priority} assignee={assignee} category={category}\n{body fragment}`.
- **Body sources:** title, description, all comments (author + text), resolution notes when RESOLVED/CLOSED.

## Embedding model

- **Default (local):** `nomic-embed-text` via Ollama (`SPRING_AI_OLLAMA_EMBEDDING_MODEL`).
- **Alternative:** OpenAI `text-embedding-3-small` via `spring.ai.openai` profile (API key from env only).

## Vector store

- **Dev default:** In-memory `SimpleVectorStore` (no Docker required).
- **Docker profile:** PostgreSQL + PGVector extension; `PgVectorStore` with same metadata keys.

## RAG query path

1. Embed question.
2. Similarity search with configurable `topK` and `similarityThreshold`.
3. If no documents pass threshold → return fixed no-match payload (no LLM).
4. Else build prompt with retrieved chunks only; LLM must cite `TKT-*` IDs present in context.
5. Post-validate: cited IDs must ⊆ retrieved ticket IDs.

## Re-ingestion

On ticket create/update/close: delete vectors where `metadata.ticketId == id`, then re-chunk and add.

## Configuration (env)

| Variable | Purpose |
|----------|---------|
| `OLLAMA_BASE_URL` | Ollama HTTP base |
| `SPRING_AI_OLLAMA_CHAT_MODEL` | Chat model name |
| `SPRING_AI_OLLAMA_EMBEDDING_MODEL` | Embedding model |
| `RAG_TOP_K` | Retrieval count |
| `RAG_SIMILARITY_THRESHOLD` | Min score (0–1) |
| `SPRING_PROFILES_ACTIVE` | `dev`, `docker`, `openai` |
