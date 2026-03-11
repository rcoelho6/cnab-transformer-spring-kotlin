# TED TRANSFERS — ENDPOINTS SUMMARY

- POST /payments/ted — inicia TED (bankCode, agency, account, holder, amount, requestId)
- GET /payments/ted/{id} — status
- Webhook: /webhooks/ted

Headers:
- Authorization: Bearer <token>
- X-Request-Id: <uuid>

