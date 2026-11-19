# REST API contract

Base: `/api`

## Tickets

| Method | Path | Description |
|--------|------|-------------|
| POST | `/tickets` | Create ticket |
| GET | `/tickets` | List; query `status`, `q` (keyword) |
| GET | `/tickets/{publicId}` | Get by public id |
| PATCH | `/tickets/{publicId}` | Update fields |
| POST | `/tickets/{publicId}/transition` | Body: `{ "status": "IN_PROGRESS" }` |
| POST | `/tickets/{publicId}/comments` | Body: `{ "author", "body" }` |

### Create ticket body

```json
{
  "title": "string",
  "description": "string",
  "priority": "MEDIUM",
  "assignee": "optional",
  "category": "optional"
}
```

### Error shape

```json
{
  "error": "VALIDATION_ERROR",
  "message": "human readable",
  "fieldErrors": [{ "field": "title", "message": "..." }]
}
```

Invalid transition: `409` with `error: "INVALID_TRANSITION"`.

## AI

| Method | Path | Body |
|--------|------|------|
| POST | `/ai/ask` | `{ "question": "string" }` |

### Response

```json
{
  "answer": "string",
  "citedTicketIds": ["TKT-1001"],
  "grounded": true
}
```

No match:

```json
{
  "answer": "No relevant tickets found in the knowledge base for your question.",
  "citedTicketIds": [],
  "grounded": false
}
```
