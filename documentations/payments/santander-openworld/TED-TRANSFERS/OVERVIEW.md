# TED TRANSFERS — OVERVIEW

Arquivos: `OVERVIEW.md`, `INTEGRATION-ASYNC.md`, `ENDPOINTS-SUMMARY.md`, `EXAMPLES.md`

Resumo:
- API para iniciar TED, consultar status e reconciliar.
- Fluxo assíncrono recomendado para processamento em lote ou por demanda.

Diagrama

```mermaid
sequenceDiagram
  participant APP
  participant MQ
  participant WORKER
  participant SANTTED
  APP->>MQ: ENQUEUE ted_payment(payload)
  WORKER->>SANTTED: POST /payments/ted
  SANTTED-->>WORKER: 202 Accepted (paymentId)
  SANTTED-->>APP: webhook /ted/status
```

