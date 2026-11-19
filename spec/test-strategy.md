# Test strategy

## Backend

- **Unit:** `TicketStateMachine` all transitions.
- **Integration:** `@SpringBootTest` + MockMvc or WebTestClient for REST + H2.
- **State machine IT:** full happy path + illegal transitions (`TicketTransitionIT`).
- **RAG IT:** `@MockBean` for `ChatModel` when testing retrieval gate; optional `@EnabledIfEnvironmentVariable` for live Ollama.

## Frontend

- Smoke: list loads, create ticket form validation (manual for assessment).

## CI (future)

- `mvn -f backend verify`
- `npm run build` in frontend
