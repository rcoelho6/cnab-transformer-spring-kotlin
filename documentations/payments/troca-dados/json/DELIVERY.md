# DELIVERY — Contrato de Entrega (REST / JMS)

Resumo
- Define como o JSON gerado pelo parser CNAB será entregue ao consumidor, e como o consumidor deve responder/acknowledge.
- Regras aplicam-se tanto para mensagens de evento (CNAB.PARSED) quanto para respostas/ACKs.

Referências de schema
- Payloads e estrutura estão especificados em `documentations/payments/dominios/json/JSON-SCHEMA.md` e `PARSER-JSON-CNAB.md`.

1) Entrega via REST (webhook)

Endpoint
- O produtor fará POST para o `callbackUrl` cadastrado com Content-Type: application/json.

Headers recomendados
- `Authorization: Bearer <token>` (se aplicável)
- `X-Signature: sha256=...` (HMAC do corpo)
- `X-Event-Id: <uuid>`
- `X-Event-Type: CNAB.PARSED`
- `X-Timestamp: 2026-03-10T12:00:00Z`

Body
- O corpo será o JSON conforme o schema em `JSON-SCHEMA.md`. Exemplo (simplificado):
```
{
  "clientUuid":"...",
  "processingId":"...",
  "records": [ /* conforme schema */ ]
}
```

Resposta esperada do consumidor
- 200 OK — recebeu e aceitou (idempotência tratada pelo `X-Event-Id`).
- 202 Accepted — aceitou para processamento assíncrono.
- 400/422 — payload inválido (o produtor deve registrar e possivelmente enviar DLQ).
- 401/403 — auth inválida; produtor pode desabilitar a subscription após N falhas.

Retry e durabilidade
- O produtor tentará entregar com retry exponencial em caso de 5xx/timeouts. Após N tentativas, colocará a mensagem em DLQ e notificará o owner da subscription.

Resposta com ACK/retorno
- Se a subscription foi criada com `acceptsResponse=true`, o consumidor pode responder com um JSON de ACK no corpo contendo status (COMPLETED/REJECTED/ERROR), `processingId` e `validationReport`.

2) Entrega via JMS

Publicação
- O produtor publicará uma mensagem na `queueName` registrada. A mensagem conterá o JSON conforme o schema e propriedades JMS (JMSMessageID, JMSTimestamp).

Propriedades recomendadas
- `eventType=CNAB.PARSED`
- `clientUuid=<uuid>`
- `processingId=<uuid>`

Resposta / ACK
- Se `replyQueue` for informada, o consumidor deverá enviar uma mensagem na `replyQueue` contendo o ACK JSON (mesmo formato do REST ACK). O produtor correlacionará via `processingId` ou JMSCorrelationID.

Transações e ack modes
- Se usar transações JMS, prefira `CLIENT_ACK` em consumidores críticos para permitir reprocessamento em falhas.

Exemplo de ACK (JSON)
```
{
  "clientUuid":"123e4567-e89b-12d3-a456-426614174000",
  "processingId":"pro-0001",
  "status":"COMPLETED|REJECTED|ERROR",
  "validationReport": { /* optional */ },
  "createdAt":"2026-03-10T12:34:56Z"
}
```

Segurança
- As práticas de segurança para entrega de JSONs internos estão centralizadas em `../../segurança/JSON-INTERNAL.md` (Shared Secret, TLS interno, JMS policies).

Observações sobre idempotência
- Usar `processingId` e `X-Event-Id` para garantir idempotência. Consumidores devem aplicar dedup quando possível.
