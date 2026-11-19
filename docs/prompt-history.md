# Prompt history

Log of significant prompts and outcomes during development.

## Session 1 — Project bootstrap

- **Prompt:** Scaffold AI support ticket system (spec-driven, Spring Boot + RAG).
- **Outcome:** Repo structure, specs, backend, minimal frontend.

## Documented AI mistake

### Mistake: RAG citation without retrieval hit

- **When:** Early `/api/ai/ask` prototype.
- **What happened:** The model answered with a plausible ticket ID (`TKT-1001`) even when vector search returned zero chunks above the similarity threshold, because the prompt did not hard-require empty retrieval → fixed response path before generation.
- **Fix:** Enforce `if (chunks.isEmpty())` return `no relevant tickets found` with HTTP 200 and **never** call the chat model; integration test `RagAskControllerIT` asserts no fabricated IDs.
- **Lesson:** Grounded RAG needs a retrieval gate in code, not only in the system prompt.
