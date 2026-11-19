# Data model

## Ticket

| Field | Type | Notes |
|-------|------|-------|
| id | Long | PK |
| publicId | String | e.g. `TKT-1001`, unique |
| title | String | required, max 200 |
| description | String | required, max 5000 |
| status | enum | OPEN, IN_PROGRESS, RESOLVED, CLOSED, CANCELLED |
| priority | enum | LOW, MEDIUM, HIGH, CRITICAL |
| assignee | String | optional, max 100 |
| category | String | optional, max 100 |
| resolutionNotes | String | optional, max 5000 |
| createdAt | Instant | |
| updatedAt | Instant | |

## Comment

| Field | Type |
|-------|------|
| id | Long |
| ticket | Ticket FK |
| author | String |
| body | String |
| createdAt | Instant |

## Vector metadata (not relational)

- `ticketId` (string public id)
- `status`, `priority`, `assignee`, `category`
- `chunkIndex` (int)
