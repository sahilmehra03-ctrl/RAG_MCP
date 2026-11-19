# AI-Powered Support Ticket Management System

Assessment project: ticket CRUD with enforced status state machine, keyword search/filter, and grounded RAG over ticket knowledge via Spring AI + Ollama.

## Prerequisites

- Java 21
- Maven 3.9+
- Node.js 20+ (frontend)
- [Ollama](https://ollama.com/) for local chat + embeddings (dev)
- Optional: Docker for PostgreSQL + PGVector (`docker compose up -d`)

Pull models (example):

```bash
ollama pull llama3.2
ollama pull nomic-embed-text
```

## IntelliJ

1. **File → Open** → select `backend/pom.xml` (open as Maven project).
2. Set Project SDK to **Java 21**.
3. Run `SupportTicketApplication` with profile `dev` (default).

## Environment variables

| Variable | Default | Purpose |
|----------|---------|---------|
| `OLLAMA_BASE_URL` | `http://localhost:11434` | Ollama API |
| `SPRING_AI_OLLAMA_CHAT_MODEL` | `llama3.2` | Chat model |
| `SPRING_AI_OLLAMA_EMBEDDING_MODEL` | `nomic-embed-text` | Embeddings |
| `RAG_TOP_K` | `5` | Retrieval count |
| `RAG_SIMILARITY_THRESHOLD` | `0.5` | Min similarity |
| `SPRING_PROFILES_ACTIVE` | `dev` | `dev` (H2 + in-memory vectors) or `docker` (Postgres + PGVector) |
| `VITE_API_BASE` | `http://localhost:8080/api` | Frontend API base |

No API keys are committed. For OpenAI instead of Ollama, use Spring AI OpenAI starter with keys from your environment only (see `spec/architecture.md`).

## Run backend

```bash
cd backend
mvn spring-boot:run
```

H2 console (dev): http://localhost:8080/h2-console

## Run frontend

```bash
cd frontend
npm install
npm run dev
```

UI: http://localhost:5173

## Tests

```bash
cd backend
mvn test
```

Includes state-machine integration tests and RAG no-match behavior (no Ollama required for tests).

## Repository layout

- `spec/` — requirements, API, state machine, RAG design
- `backend/` — Spring Boot API
- `frontend/` — Vite + React UI
- `docs/prompt-history.md` — prompts + documented AI mistake
- `.cursor/` — rules and review commands

## Git remote

After creating your GitHub repo:

```bash
git remote add origin git@github.com:YOUR_USER/ai-support-tickets.git
git push -u origin main
```

## Workflow

Requirement → Specification (`spec/`) → Plan → Implementation → Testing → Review. See acceptance checklist in `spec/requirements.md`.
