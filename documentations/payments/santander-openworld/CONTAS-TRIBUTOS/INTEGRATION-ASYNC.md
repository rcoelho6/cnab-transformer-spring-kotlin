# CONTAS E TRIBUTOS — INTEGRATION ASYNC

Fluxo:
- Producer enfileira `tax_payment` com `requestId`, codigoTributo, valor, datos do pagador.
- Worker processa e chama API Santander para pagar tributo ou gerar guia.
- Reconciliador consulta status e atualiza sistema.

Observações:
- Validar códigos de tributo e formatos (DARF/GPS/GNRE).
- Atentar para horários de processamento e compensação.


