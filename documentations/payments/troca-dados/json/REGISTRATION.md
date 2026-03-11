# REGISTRATION — Como se inscrever para receber o JSON gerado pelo parser CNAB

Visão geral
- Aplicações externas podem se registrar para receber os JSONs produzidos pelo parser CNAB.
- Dois modos de registro suportados:
  1. REST: o consumidor registra um endpoint HTTP (webhook) que receberá POSTs com o JSON de evento e, opcionalmente, um arquivo de resposta (retorno) será enviado para o mesmo webhook.
  2. JMS: o consumidor informa uma fila (JMS) onde deseja receber os eventos; o produtor enviará o JSON para essa fila e aguardará a mensagem de resposta na fila de retorno indicada.

Pré-requisitos
- Ter um `clientId` e credenciais (token/secret) configuradas no provedor de integração.
- Conhecer o schema do JSON (veja `documentations/payments/dominios/json/JSON-SCHEMA.md`).

1) Registro via REST (webhook)

- Endpoint de registro (exemplo): POST /api/v1/subscriptions

Payload de registro (REST)
```
{
  "clientUuid": "<uuid>",
  "mode": "REST",
  "callbackUrl": "https://consumer.example.com/cnab/events",
  "callbackAuth": { "type":"HMAC|Bearer", "secretId":"<id>" },
  "events": ["CNAB.PARSED"],
  "acceptsResponse": true
}
```

- `callbackUrl` será verificada (see-verification).
- `acceptsResponse=true` indica que o consumidor quer receber a resposta/ACK (ex.: retContentBase64 or JSON response) no mesmo webhook.

Verificação do webhook
- O provedor enviará um challenge (GET/POST) com token para o `callbackUrl`. O consumidor deve responder com 200 OK e o token (ou assinar com a chave acordada).

2) Registro via JMS (fila)

- Payload de registro (JMS)
```
{
  "clientUuid":"<uuid>",
  "mode":"JMS",
  "queueName":"consumer.incoming.cnab",
  "replyQueue":"consumer.outgoing.cnab.ret",
  "ackMode":"AUTO|CLIENT"
}
```

- `queueName` é a fila onde o produtor publicará os JSONs.
- `replyQueue` é a fila onde o consumidor espera as respostas/ACKs (opcional, mas recomendada quando `acceptsResponse` for true).

Segurança e autenticação
- As recomendações de segurança para trocas de JSONs internos foram consolidadas em `../../segurança/JSON-INTERNAL.md`. Consulte esse arquivo para diretrizes sobre Shared Secret (HMAC), TLS interno, e práticas de filas/JMS.

Políticas e limites
- Rate limits e quotas podem ser aplicadas por `clientUuid`.
- Para REST, o produtor pode limitar simultaneidade e tamanho do payload; se o consumidor retornar 429, o produtor aplicará backoff.

Cancelamento e atualização
- Para remover ou atualizar uma inscrição, usar DELETE /api/v1/subscriptions/{id} ou PATCH com novos parâmetros.

Observações
- Todos os formatos de payload seguem o schema documentado em `documentations/payments/dominios/json/JSON-SCHEMA.md`.
