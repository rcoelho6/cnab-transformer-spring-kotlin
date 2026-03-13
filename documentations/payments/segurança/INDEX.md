# Segurança — Pagamentos

Esta seção contém as diretrizes e recomendações consolidadas de segurança para todos os canais de troca de dados de pagamentos.

## Áreas de Foco

A segurança é dividida em dois grandes pilares, dependendo da origem e do destino dos dados:

### 1. Troca de Arquivos CNAB (Externo)
Focada na segurança entre parceiros externos e o sistema de recebimento.
-   **[CNAB-EXCHANGE.md](CNAB-EXCHANGE.md)**
    -   Segurança para canais como SFTP, HTTP-BASE64, PUSH-NOTIFY e Cloud Storage (S3/GCS).
    -   Recomendações de mTLS, OAuth2 e chaves SSH.

### 2. Troca de JSON (Interno)
Focada na segurança entre microserviços e sistemas internos após o processamento do CNAB.
-   **[JSON-INTERNAL.md](JSON-INTERNAL.md)**
    -   Segurança para webhooks internos, mensageria JMS e comunicação REST intra-VPC.
    -   Uso de Shared Secrets (HMAC) e TLS interno.

## Recomendações Gerais

Consulte cada documento para detalhes operacionais específicos, incluindo:
-   **Autenticação e Autorização**: Como garantir a identidade das partes.
-   **Integridade**: Uso de assinaturas e checksums.
-   **Auditoria**: Logging e monitoramento de acessos.

---

![Fluxo de Segurança](../images/security_flow.jpg)

*Visão geral das camadas de segurança aplicadas aos canais de pagamento.*
