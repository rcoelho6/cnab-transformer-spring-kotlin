# TRANSFERENCIAS INTELIGENTES — ENDPOINTS SUMMARY

(Resumo genérico — adapte ao contrato do Santander)

- POST /smart-transfers — inicia transferência inteligente (requestId, amount, preferredMethods, split)
- GET /smart-transfers/{id} — status consolidado
- Webhook: /webhooks/smart-transfers

Headers: Authorization, X-Request-Id, Idempotency-Key

