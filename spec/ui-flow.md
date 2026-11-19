# UI flows

## Ticket list

- Table: public id, title, status, priority, assignee.
- Filter by status dropdown.
- Search box (keyword → `q` param).

## Ticket detail

- View/edit title, description, priority, assignee, category, resolution notes (when allowed).
- Status transitions via buttons (only legal next states).
- Comments thread + add comment.
- Show API validation errors inline.

## Ask AI

- Single question input → POST `/api/ai/ask`.
- Display answer + cited ticket links (navigate to detail).
