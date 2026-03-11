# HTTP-BASE64 — EXAMPLES

Exemplo request (curl)

```bash
curl -X POST https://api.exemplo.com/api/v1/cnab/upload \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -H "X-Client-Uuid: 123e4567-e89b-12d3-a456-426614174000" \
  -d '{ "clientUuid":"123e4567-e89b-12d3-a456-426614174000","fileName":"cnab.txt","contentBase64":"<BASE64>" }'
```

Resposta (202 async)

```json
{ "status":"ACCEPTED_ASYNC","processingId":"pro-0001" }
```

Webhook (callback) exemplificado

```json
POST /callbacks/cnab/result
{
  "processingId":"pro-0001",
  "status":"COMPLETED",
  "validationReport": { ... }
}
```


