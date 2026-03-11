# PIX TRANSFERS — ENDPOINTS SUMMARY

- POST /pix/payments — inicia pagamento PIX (body: amount, key, requestId, schedule)
- GET /pix/payments/{txId} — status do pagamento
- POST /pix/refunds — solicita estorno
- Webhook: /webhooks/pix — notificações de sucesso/estorno

Headers:
- Authorization: Bearer <token>
- X-Request-Id: <uuid>
- Idempotency-Key: <uuid>

