# Segurança — Troca de JSON (sistemas internos)

Escopo
- Recomendações para troca de JSONs gerados pelo parser CNAB entre sistemas internos (microserviços, filamentos, integrações internas) via REST interno ou JMS.

Princípio chave
- Assumimos comunicação dentro de rede/control plane interna (VPC, trusted network). Para esses casos o uso de Shared Secret (HMAC) combinado com TLS interno é adequado e mais simples de operar.

Recomendações principais

1) Autenticação e assinatura
- Shared Secret (HMAC SHA-256) — cada consumidor tem um secret armazenado no Secret Manager. O produtor assina o corpo do JSON e inclui `X-Signature: sha256=...`.
- TLS interno obrigatório (intra-VPC TLS/TLS mutual optional depending on environment).

2) Entrega via JMS / fila
- Use filas autenticadas e isoladas por ambiente e por cliente lógico.
- Use JMSCorrelationID or processingId para correlação de resposta.
- Preferir transações e `CLIENT_ACK` quando a garantia de processamento for necessária.

3) Webhooks internos (REST)
- Autenticação via Shared Secret (HMAC) ou mTLS se a comunicação cruzar boundary entre clusters.
- Validar `X-Event-Id` e `processingId` para evitar duplicatas.

4) Idempotência e deduplicação
- Consumidores devem ser idempotentes; use `processingId` para dedup.

5) Rotinas operacionais
- Secrets em Secret Manager; rota de rotação automática.
- Monitoramento de filas (depth, age) e alerting para mensagens em DLQ.

Possíveis variações e opções adicionais
- Mutual TLS interno quando se quer garantia de identidade entre serviços sem centralizar em tokens.
- OAuth2 with internal identity provider (Keycloak, Azure AD) quando há necessidade de RBAC e expiração de tokens.
- Use of message-level encryption (e.g., JWE) for extra confidentiality in shared infra.

Outras propostas cabíveis
- Short-lived tokens issued by internal STS for services that prefer token-based auth.
- Signed JWTs with audience/issuer claims for cross-cluster routing and RBAC enforcement.
- Network policies (Kubernetes NetworkPolicy) to restrict egress/ingress between service pairs.

