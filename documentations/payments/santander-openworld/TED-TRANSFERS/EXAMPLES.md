# TED TRANSFERS — EXAMPLES

Example payload

```json
{
  "requestId": "ted-0001",
  "bankCode": "001",
  "agency": "1234",
  "account": "00012345-6",
  "holder": {"name":"Fulano","document":"12345678901"},
  "amount": 100000
}
```

Webhook

```json
{ "paymentId": "p-0001", "requestId":"ted-0001","status":"SETTLED" }
```

