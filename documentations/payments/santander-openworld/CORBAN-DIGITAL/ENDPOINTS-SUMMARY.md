# CORBAN DIGITAL — ENDPOINTS SUMMARY

(Resumo genérico; adapte ao contrato real do Santander no Developer Portal)

- POST /charges — registra cobrança (requestId, amount, dueDate, payer)
- GET /charges/{id} — consulta status
- POST /charges/{id}/cancel — solicita cancelamento/baixa
- GET /charges/{id}/events — eventos/retornos
- Webhook: /webhooks/charges — notificações de status

Headers comuns:
- Authorization: Bearer <token>
- X-Request-Id: <uuid>
- Content-Type: application/json


