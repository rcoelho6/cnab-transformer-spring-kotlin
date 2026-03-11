# Segurança — Troca de arquivos CNAB (externos)

Escopo
- Recomendações para integração com sistemas externos que enviam arquivos CNAB via SFTP, HTTP-BASE64 (API), Push-Notify (API), ou Cloud Storage (S3/GCS).
- Objetivo: proteger confidencialidade, integridade e disponibilidade dos arquivos e dos mecanismos de notificação/retorno.

Princípios gerais
- TLS 1.2+ obrigatório em todos endpoints HTTP; preferir TLS 1.3 quando possível.
- Autenticação mútua (mutual TLS) ou OAuth2 Client Credentials com client-id/secret para canais REST expostos a terceiros.
- Para transferência de arquivos: preferir key-based auth (SFTP) ou pre-signed URLs para S3 em vez de credenciais estáticas.
- Segregar ambientes e políticas por `clientUuid` — isolamento de prefixos/buckets/pastas e quotas.

Recomendações por canal

1) SFTP
- Método preferido para parceiros que suportam chaves SSH.
- Autenticação: SSH key-based (cert-authority when possible). Não usar senhas.
- Isolamento: chroot por usuário, diretórios separados por `clientUuid`.
- Logging detalhado e integridade: gerar checksums (sha256) após upload; usar temp-file + rename pattern para sinalizar upload completo.
- Rotação de chaves: provisionamento/rotacionamento automatizado; revogar chaves comprometidas.

2) Cloud Storage (S3 / GCS)
- Use pre-signed URLs para upload com TTL curto quando o cliente não pode ter credenciais.
- Para integrações com credenciais, use IAM roles e políticas por prefixo `incoming/<clientUuid>/`.
- Habilitar server-side encryption e eventos de objeto para validação/assincronização.

3) HTTP-BASE64 (API)
- Autenticação recomendada: OAuth2 Client Credentials (client-id + secret) ou mTLS para requisitos de segurança mais elevados.
- Idempotency: requerer `Idempotency-Key` e `X-Client-Uuid` no header.
- Proteções adicionais: rate limiting por `clientUuid`, tamanho máximo do payload, escopo de validação antes de decodificar (evitar DoS por base64). Validate incoming base64 length and memory usage.
- Webhook de retorno: exigir assinatura HMAC (`X-Signature`) ou entregar via mTLS.

4) PUSH-NOTIFY (API que informa local do arquivo)
- Similar ao HTTP-BASE64 no que tange à autenticação (Bearer token ou mTLS).
- Validar que `filePath` pertence ao `clientUuid` e checar checksum fornecido.

Autenticação: quando usar client-id/secret vs certificados vs VPN
- Client-id + secret (OAuth2 Client Credentials)
  - Bom equilíbrio entre segurança e facilidade de integração para parceiros externos que não suportam certificados.
  - Requer rotação periódica de secrets e armazenamento seguro (secret manager).
  - Use scopes/claims para limitar permissões por client.

- Mutual TLS (mTLS) com certificado de cliente
  - Preferível para parceiros de alto nível de confiança e quando se exige forte autenticação e proteção contra tokens comprometidos.
  - Requer gestão de PKI e processos de emissão/renovação/revogação de certificados.
  - Use quando a exposição de endpoints for pública e o risco de interceptação for significativo.

- VPN + Certificados
  - Útil quando o parceiro for uma integração B2B com comunicação restrita por rede privada (VPN/IPSec) e se desejar uma camada de rede adicional.
  - Combine com mTLS ou client-credentials para autenticação de aplicação.

Recomendação prática por cenário
- Pequenos parceiros / integrations públicos: OAuth2 Client Credentials + TLS + HMAC para webhooks.
- Parceiros corporativos / integrações críticas: mTLS + VPN quando aplicável.
- Transfers via SFTP: SSH key-based + chroot.

Autorização e isolamento
- Policies por prefix (S3) ou by-client folders (SFTP/SMB).
- Lista de allowed webhook domains; registrar e validar `webhookUrl` em processo de subscription.

Logging, auditoria e monitoramento
- Log de acesso (who/when/file), checksum, tamanho e IP de origem.
- Monitorar taxas de erro e spikes (DoS detection).
- Gerar alertas para uploads falhos repetidos e atividades suspeitas.

Retorno e webhooks seguros
- Webhooks: HMAC SHA-256 do corpo com secret por `clientUuid` (header `X-Signature: sha256=...`).
- Fornecer `X-Timestamp` e `X-Request-Id` para evitar replay; validar janela de recepção (ex.: 5 minutos).
- Retries: aplicar backoff e DLQ para falhas persistentes.

Operações de chave e ciclo de vida
- Manter secrets em Secret Manager (AWS Secrets Manager, Azure Key Vault, HashiCorp Vault).
- Rotação automática quando possível; ter processo de revogação e emergency rotation.

Outras propostas cabíveis (apêndice)
- IP allowlist para parceiros conhecidos.
- Rate limits por clientUuid com planos diferenciados.
- WAF para endpoints HTTP para mitigar payloads maliciosos.
- DLP/Scanning de conteúdo para detectar dados sensíveis acidentalmente enviados.

