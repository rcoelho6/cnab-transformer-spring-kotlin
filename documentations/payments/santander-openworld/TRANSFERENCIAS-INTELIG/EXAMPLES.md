# TRANSFERENCIAS INTELIGENTES — EXAMPLES

Example payload

```json
{
  "requestId":"smart-0001",
  "amount": 10000,
  "preferredMethods":["PIX","TED"],
  "split": [ {"sellerId":"s1","amount":7000}, {"sellerId":"s2","amount":3000} ]
}
```

Webhook example

```json
{ "id":"smart-0001","status":"COMPLETED","attempts":[{"method":"PIX","status":"FAILED"},{"method":"TED","status":"COMPLETED"}] }
```

