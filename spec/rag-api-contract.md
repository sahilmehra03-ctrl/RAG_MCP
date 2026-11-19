# RAG API contract (detail)

## POST /api/ai/ask

**Request**

```json
{ "question": "What caused payment failures in high priority tickets?" }
```

**Success (grounded)**

- `answer`: natural language, must reference only retrieved content.
- `citedTicketIds`: subset of IDs appearing in retrieved metadata.
- `grounded`: `true`

**No retrieval**

- Fixed answer string (see `api-contract.md`).
- `grounded`: `false`
- HTTP 200 (not an error — honest empty result).

**Validation**

- `question`: required, 3–2000 chars → `400` if invalid.

## Configuration

`application.yml`:

```yaml
app.rag.top-k: ${RAG_TOP_K:5}
app.rag.similarity-threshold: ${RAG_SIMILARITY_THRESHOLD:0.5}
```
