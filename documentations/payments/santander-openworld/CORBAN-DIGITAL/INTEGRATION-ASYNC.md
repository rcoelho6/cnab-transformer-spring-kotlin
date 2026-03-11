# CORBAN DIGITAL — INTEGRATION ASYNC

Fluxo recomendado (assíncrono):

1. Producer: valida payload e enfileira mensagem `create_charge` (JSON) na fila.
2. Worker: consome mensagem, chama endpoint Santander para registrar cobrança.
3. Santander retorna 202+jobId; worker enfileira `reconcile` com jobId.
4. Reconciliador: verifica status do job via API ou aguarda webhook; atualiza DB e notifica cliente.

Regras operacionais:
- Idempotência: incluir `requestId` para evitar duplicação.
- Retry: política exponencial para falhas transitórias.
- Dead Letter Queue: mensagens com falha repetida vão para DLQ para análise manual.
- Observability: logs correlacionados por `requestId` e métricas (latência, erros).


