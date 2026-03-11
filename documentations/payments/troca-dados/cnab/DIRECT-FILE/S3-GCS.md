# DIRECT-FILE — Cloud Storage (S3 / GCS) — Proposta

Fluxo (S3 example)
- Cliente faz PUT em bucket S3 com prefixo `incoming/<clientUuid>/file.cnab` (usando pre-signed URL ou credentials)
- Evento S3 (OBJECT_CREATED) dispara Lambda/Function que valida e move para processing
- System writes ACK to `incoming/<clientUuid>/acks/ack-<file>.json` or callback via webhook

Segurança
- Recomendações consolidadas em `../../segurança/CNAB-EXCHANGE.md` (IAM policies por prefixo, pre-signed URLs, SSE, etc.).

Vantagens
- Escalabilidade, durabilidade, integra com event-driven
- Fácil integração com serverless

Desvantagens
- Requer gestão de buckets e IAM; latência de event vs immediate ACK


