# HTTP-BASE64 — API SPEC

Endpoint (exemplo)
- POST /api/v1/cnab/upload

Headers
- Authorization: Bearer <token> (OAuth2)
- Content-Type: application/json
- X-Client-Uuid: <uuid-v4> (obrigatório)
- Idempotency-Key: <uuid> (opcional, recomendado)

Payload (JSON)
```
{
  "clientUuid": "<uuid-v4>",
  "fileName": "cnab_20260310_001.txt",
  "contentBase64": "<base64-string>",
  "metadata": { "source": "erp-x", "receivedAt": "2026-03-10T12:00:00Z" }
}
```

Responses
- 200 OK (sync, file validated and accepted)
```
{ "status":"ACCEPTED", "processingId":"<uuid>", "validationReport": {...} }
```
- 202 Accepted (async)
```
{ "status":"ACCEPTED_ASYNC", "processingId":"<uuid>" }
```
- 400 Bad Request (invalid payload)
- 401/403 (auth)
- 413 Payload Too Large (server limit)

Processing semantics
- On receipt: validate envelope (clientUuid exists) -> base64 decode (validate bytes) -> run pre-validation (`VALIDACOES-CNAB.md`) -> if FATAL -> return 400 with report or 200 with status=REJECTED; if OK -> enqueue file for parsing and processing -> return 202 with processingId

Retry and Idempotency
- Client should use `Idempotency-Key` when retrying upload to avoid duplicate processing.

Webhook (retorno / callback)
- Quando o processamento termina (sync ou async) o sistema pode enviar um POST ao `webhookUrl` informado no envelope ou cadastrado para o `clientUuid` com o arquivo de retorno codificado em Base64.

Headers recomendados para o callback
- `Content-Type: application/json`
- `X-Signature: sha256=...` (HMAC do corpo) — assinar com o secret do cliente
- `X-Timestamp` e `X-Request-Id` para evitar replay

Body do webhook (JSON)
```
{
  "clientUuid": "123e4567-e89b-12d3-a456-426614174000",
  "processingId": "pro-0001",
  "originalFileName": "cnab_20260310_001.txt",
  "retFileName": "cnab_20260310_001.RET",
  "retContentBase64": "<base64-of-ret-cnab>",
  "validationReport": { /* optional */ }
}
```

Semântica
- Se o webhook responder 200 OK, o envio é considerado entregue; se 5xx ou timeout, aplicar retry com backoff. Após N falhas, colocar em DLQ e gerar fallback (ex.: armazenar em bucket e avisar por email).

Segurança e validação
- As recomendações de segurança para este fluxo foram consolidada em `../segurança/CNAB-EXCHANGE.md` e devem ser seguidas (webhooks, autenticação, TLS, HMAC, idempotency).


