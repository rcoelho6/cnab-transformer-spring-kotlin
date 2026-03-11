# PIX TRANSFERS — EXAMPLES

Example payload

```json
{
  "requestId": "pix-0001",
  "amount": 5000,
  "key": "fulano@bank.com",
  "description": "Pagamento fatura",
  "schedule": null
}
```

Webhook example

```json
{
  "txId": "tx-0001",
  "requestId": "pix-0001",
  "status": "CONFIRMED",
  "timestamp": "2026-03-10T12:00:00Z"
}
```

