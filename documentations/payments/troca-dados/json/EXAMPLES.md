# EXAMPLES — Exemplos práticos de registro e entrega

1) Registrar via REST (curl)

```bash
curl -X POST https://api.example.com/api/v1/subscriptions \
  -H "Authorization: Bearer <admin-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "clientUuid":"123e4567-e89b-12d3-a456-426614174000",
    "mode":"REST",
    "callbackUrl":"https://consumer.example.com/cnab/events",
    "callbackAuth": {"type":"HMAC","secretId":"s1"},
    "events":["CNAB.PARSED"],
    "acceptsResponse":true
  }'
```

2) Exemplo de payload entregue (veja também `documentations/payments/dominios/json/EXAMPLE-CNAB.JSON`)

```json
{
  "clientUuid":"123e4567-e89b-12d3-a456-426614174000",
  "processingId":"pro-0001",
  "records": []
}
```

3) Exemplo de registro via JMS (JSON administratively sent to provider)

```json
{
  "clientUuid":"123e4567-e89b-12d3-a456-426614174000",
  "mode":"JMS",
  "queueName":"consumer.incoming.cnab",
  "replyQueue":"consumer.outgoing.cnab.ret",
  "ackMode":"CLIENT"
}
```

4) Exemplo de verificação de webhook (challenge)

O provedor fará um POST para `callbackUrl` com `{ "challenge": "<token>" }` e o consumidor deverá responder com 200 OK e o mesmo token para confirmar posse.

Veja o exemplo completo de payload gerado pelo parser em `documentations/payments/dominios/json/EXAMPLE-CNAB.JSON`.


