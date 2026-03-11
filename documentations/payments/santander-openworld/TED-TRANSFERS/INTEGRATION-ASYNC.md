# TED TRANSFERS — INTEGRATION ASYNC

Fluxo:
- Producer valida e enfileira `ted_payment`.
- Worker consome e chama `/payments/ted` com idempotency `requestId`.
- Ao receber 202 + paymentId, enfileira `reconcile_ted`.
- Reconciliador consulta status e atualiza sistema.

Operações críticas:
- Formato banco/agência/conta/ CPF/CNPJ do favorecido
- Prazos de liquidação e cutoffs


