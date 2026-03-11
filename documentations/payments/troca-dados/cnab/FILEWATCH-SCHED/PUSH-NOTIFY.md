# PUSH-NOTIFY — Contrato de Notificação (API)

Quando usar
- Cliente envia um arquivo para local acordado (S3, SFTP, rede) e então chama a API de notificação para informar disponibilidade.

Endpoint (exemplo)
- POST /api/v1/cnab/notify

Headers
- Authorization: Bearer <token>
- X-Client-Uuid: <uuid>

Payload
```
{
  "clientUuid": "123e4567-e89b-12d3-a456-426614174000",
  "filePath": "/incoming/123e.../cnab_20260310_001.cnab",
  "checksum": "sha256:...",
  "fileSize": 123456
}
```

Resposta
- 202 Accepted + processingId

Processamento
- Validar origem do filePath (prefix corresponde ao clientUuid)
- Validar checksum and fileSize
- Enfileirar para processamento

Segurança
- Recomendações consolidadas em `../../segurança/CNAB-EXCHANGE.md` (autenticação, validação de token, rate limits, validação de paths e checksum).

Return path / ACK
- O payload de notificação pode opcionalmente incluir `returnPath` indicando onde o cliente deseja que o `.RET` seja gravado após processamento.

Exemplo estendido de payload
```
{
  "clientUuid": "123e4567-e89b-12d3-a456-426614174000",
  "filePath": "/incoming/123e.../cnab_20260310_001.cnab",
  "checksum": "sha256:...",
  "fileSize": 123456,
  "returnPath": "/outgoing/123e.../cnab_20260310_001.RET"
}
```

Regras
- `returnPath` deve apontar para um local permitido para aquele `clientUuid` (prefix/policy).
- Após gerar o arquivo de retorno, o servidor grava o arquivo no `returnPath` com extensão `.RET` e cria um ACK `returnPath + .ack.json` contendo `processingId` e `validationReport`.

Exemplo de ACK
```
{
  "processingId":"pro-0001",
  "status":"COMPLETED",
  "retFile":"/outgoing/123e.../cnab_20260310_001.RET"
}
```

Se a gravação falhar, o servidor deve emitir callback de erro ou colocar o arquivo em DLQ e notificar o cliente via webhook/email.


