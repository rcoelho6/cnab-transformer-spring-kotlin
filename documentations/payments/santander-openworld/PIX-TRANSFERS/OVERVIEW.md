# PIX TRANSFERS — OVERVIEW

Arquivos nesta pasta:
- `OVERVIEW.md`
- `INTEGRATION-ASYNC.md`
- `ENDPOINTS-SUMMARY.md`
- `EXAMPLES.md`

Resumo:
- API para iniciar pagamentos Pix, checar status, e receber notificações (webhooks).
- Suportar tipos: Cob (cobrança), Pix instantâneo, Pix Agendado, devoluções.

Diagrama

```mermaid
sequenceDiagram
  participant APP
  participant MQ
  participant WORKER
  participant SANTPIX
  APP->>MQ: ENQUEUE pix_payment(payload)
  WORKER->>SANTPIX: POST /pix/payments
  SANTPIX-->>WORKER: 202 Accepted (txId)
  SANTPIX-->>APP: webhook /pix/status
```

