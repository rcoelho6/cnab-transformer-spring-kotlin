# CORBAN DIGITAL — EXAMPLES

Example create_charge payload

```json
{
  "requestId": "cafebabe-0001-0000-0000-000000000001",
  "amount": 12345,
  "currency": "BRL",
  "dueDate": "2026-03-20",
  "payer": {
    "name": "Fulano",
    "document": "12345678901"
  },
  "instructions": "Não aceitar desconto"
}
```

Example webhook (status update)

```json
{
  "jobId": "job-0001",
  "requestId": "cafebabe-0001-0000-0000-000000000001",
  "status": "CONFIRMED",
  "receivedAt": "2026-03-10T12:34:56Z"
}
```

