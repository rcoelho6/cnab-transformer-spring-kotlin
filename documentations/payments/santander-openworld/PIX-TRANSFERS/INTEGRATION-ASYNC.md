# PIX TRANSFERS — INTEGRATION ASYNC

Recomendações:
- Enfileirar pedido `pix_payment` com `requestId`, payload e preferências (immediate/scheduled).
- Worker faz POST em `/pix/payments` com idempotency `requestId`.
- Ao receber 202 com `txId`, enfileirar `reconcile_pix`.
- Reconciliador consulta status e trata webhooks (confirmação/estorno).

Restrições operacionais:
- Tempo de vida do QR/txId para agendamentos.
- Regras de validação de chave PIX e limites.

Boas práticas:
- Validar formato da chave PIX antes de chamar API.
- Implementar retries, DLQ e métricas.


