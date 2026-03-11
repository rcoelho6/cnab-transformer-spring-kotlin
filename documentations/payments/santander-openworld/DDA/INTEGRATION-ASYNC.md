# DDA — INTEGRATION ASYNC

Fluxo recomendado:
- Enfileirar apresentação de título (requestId, dados do título).
- Worker chama endpoint de apresentação; recebe jobId.
- Acompanhamento via webhook ou polling; reconciliar aceites/recusas.

Recomendações:
- Garantir idempotência por `requestId`.
- Implementar DLQ para títulos com falha repetida.


