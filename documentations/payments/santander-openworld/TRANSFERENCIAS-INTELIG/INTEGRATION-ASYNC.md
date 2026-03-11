# TRANSFERENCIAS INTELIGENTES — INTEGRATION ASYNC

Fluxo recomendado (assíncrono):

1. Producer enfileira `smart_transfer` com `requestId`, `amount`, `preferredMethods` (ex: [PIX, TED]), `split` (opcional).
2. Worker consome e executa tentativa preferencial (ex.: PIX). Se retornar erro ou timeout, aplica fallback (ex.: TED).
3. Para cada tentativa, worker registra evento e enfileira reconcile/job para acompanhar status.
4. Reconciliador consolida resultados, aplica split payouts se necessário e notifica cliente via webhook/queue.

Regras:
- Idempotência por `requestId`.
- Política de retry e backoff para falhas transitórias.
- Dead Letter Queue para casos que exigem intervenção manual.

Boas práticas:
- Incluir metadata (marketplaceId, sellerIds) para permitir split automatizado.
- Registrar rationale de fallback para auditoria.

