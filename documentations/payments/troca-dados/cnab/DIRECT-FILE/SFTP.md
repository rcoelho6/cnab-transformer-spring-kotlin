# DIRECT-FILE — SFTP (Proposta)

Fluxo
- Cliente faz autenticação por chave pública (SSH key) e realiza PUT no diretório `/incoming/<clientUuid>/`.
- Servidor processa novos arquivos via inotify/filewatch ou job scheduler e move para `/processing/<clientUuid>/`
- Servidor gera ACK (arquivo `.ack` com processingId) ou envia callback via API

Segurança
- Recomendação consolidada em `../../segurança/CNAB-EXCHANGE.md` (SSH key management, chroot, logging, key rotation, etc.).

Considerações operacionais
- Gerenciar quotas por clientUuid
- Rotear uploads para storage durável (S3) após ACK
- Detecção de arquivos incompletos: padrão adoptado `upload.tmp` -> renomear para `file.cnab` quando upload concluído

Exemplo de ACK format
```
ack-<originalFileName>.json
{ "clientUuid":"...","processingId":"...","receivedAt":"..." }
```

