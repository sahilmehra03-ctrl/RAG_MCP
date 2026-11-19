# RAG evaluation strategy

## Manual golden questions

| Question | Expected behavior |
|----------|-------------------|
| Payment failures | Citations from payment-related tickets if present |
| Resolution for TKT-1001 | Cites TKT-1001 if ingested |
| Shipment tracking causes | Relevant logistics tickets or no-match |
| Similar resolved tickets | RESOLVED status in metadata filter optional future |
| High priority payment tickets | HIGH/CRITICAL + payment keywords |

## Automated checks

- Integration test: empty store → no-match, empty citations.
- Integration test: seeded ticket → ask by title keyword → cites correct `publicId`.
- Unit test: transition matrix for state machine.

## Hallucination guard

- Code path skips LLM when retrieval empty.
- Parse `TKT-\d+` from answer; assert ⊆ retrieved IDs.
