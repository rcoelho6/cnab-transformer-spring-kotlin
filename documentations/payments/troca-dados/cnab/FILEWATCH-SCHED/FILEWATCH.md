# FILEWATCH — Monitor de Pastas (Proposta técnico)

Implementação recomendada
- Linux: usar inotify / systemd service + script ou daemon (Go/Java)
- Java: `WatchService` (nio.file) com pool para processar eventos
- Node: `chokidar` ou `fs.watch` com debounce

Fluxo
1. Monitora `incomingRoot/<clientUuid>/` para `CLOSE_WRITE` events (arquivo finalizado)
2. Ao detectar arquivo: validar tamanho múltiplo de 240, calcular hash, renomear para `processing/` e criar `processing/<file>.lock`
3. Enfileirar processamento com metadata: clientUuid, filePath, hash
4. Ao completar: mover para `archive/<clientUuid>/` e gerar ACK (arquivo ou webhook)

Resiliência
- Dedup por hash
- Retries: em caso de falha mover para `error/<clientUuid>/` e criar uma entrada no DLQ

Exemplo de layout de pastas
```
/incoming/<clientUuid>/*.cnab
/processing/<clientUuid>/*.cnab
/archive/<clientUuid>/*.cnab
/error/<clientUuid>/*.cnab
```

Retorno e backup
- Padrão de pastas por cliente sugerido: `ROOT/<clientUuid>/ENVIO/`, `ROOT/<clientUuid>/RETORNO/`, `ROOT/<clientUuid>/ENVIO/bkp/`.
- Ao processar, o FileWatch deverá gerar o arquivo de retorno com a mesma base do arquivo original e extensão `.RET` (ex.: `cnab_20260310_001.RET`) em `RETORNO/` e mover o original para `ENVIO/bkp/<timestamp>_cnab_20260310_001.cnab`.

Exemplo de layout de pastas com retorno
```
/root/
  123e4567-e89b-12d3-a456-426614174000/
	ENVIO/
	  cnab_20260310_001.cnab
	RETORNO/
	  cnab_20260310_001.RET
	ENVIO/bkp/
	  2026-03-10T12-00-00_cnab_20260310_001.cnab
```

Regras operacionais de retorno
1. Detectar apenas arquivos finalizados (`CLOSE_WRITE` ou temp-to-final rename pattern)
2. Validar integridade e tamanho
3. Calcular checksum e comparar (se fornecido)
4. Enfileirar e processar
5. Gerar `cnab_....RET` em `RETORNO/`
6. Mover original para `ENVIO/bkp/<timestamp>/`
7. Gerar `ACK` (arquivo JSON) em `RETORNO/` ou enviar webhook/notify

Considerações de retenção e auditoria
- Backup obrigatório para auditoria e recuperação. Gerenciar retenção de `bkp/` conforme política de retenção; considerar integração com S3 lifecycle.

Segurança
- Recomendações relacionadas a autenticação, segregação por `clientUuid`, e práticas de integridade (checksum) foram centralizadas em `../../segurança/CNAB-EXCHANGE.md`.


