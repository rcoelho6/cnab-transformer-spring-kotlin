# Segurança — Troca de JSON (Sistemas Internos)

Este documento estabelece as diretrizes de segurança para a troca de dados JSON gerados pelo parser CNAB entre microserviços e sistemas internos.

## Escopo e Princípios

A comunicação ocorre dentro de uma rede confiável (VPC ou rede interna). O foco é garantir a autenticidade e integridade dos dados sem a complexidade de certificados externos.

![Fluxo de JSON Interno](../images/internal_json_flow.jpg)

### Princípio Chave
Assumimos que a comunicação ocorre em um ambiente controlado. O uso de **Shared Secret (HMAC)** combinado com **TLS interno** é a abordagem recomendada por ser segura e simples de operar.

## Recomendações Principais

### 1. Autenticação e Assinatura (HMAC)
-   **Shared Secret**: Cada consumidor possui um segredo único armazenado em um **Secret Manager**.
-   **Assinatura**: O produtor assina o corpo do JSON e inclui o header `X-Signature: sha256=...`.
-   **TLS Interno**: Uso obrigatório de TLS para criptografia em trânsito dentro da VPC.

### 2. Entrega via Mensageria (JMS / Fila)
-   **Isolamento**: Filas autenticadas e segregadas por ambiente e cliente lógico.
-   **Correlação**: Uso de `JMSCorrelationID` ou `processingId` para rastreabilidade.
-   **Garantia**: Preferência por transações e `CLIENT_ACK` para evitar perda de mensagens.

### 3. Webhooks Internos (REST)
-   **Autenticação**: Shared Secret (HMAC) ou mTLS para comunicações entre clusters.
-   **Deduplicação**: Validação de `X-Event-Id` e `processingId` para evitar processamento duplicado.

## Operações e Monitoramento

-   **Gestão de Segredos**: Uso de ferramentas como AWS Secrets Manager ou HashiCorp Vault com rotação automática.
-   **Idempotência**: Todos os consumidores devem ser idempotentes, utilizando o `processingId` como chave de deduplicação.
-   **Auditoria**: Monitoramento de profundidade de filas e alertas para mensagens em **DLQ (Dead Letter Queue)**.

## Opções Adicionais de Segurança

| Método | Quando Usar |
| :--- | :--- |
| **mTLS Interno** | Garantia de identidade entre serviços sem tokens centrais. |
| **OAuth2 Interno** | Necessidade de RBAC (Role-Based Access Control) e expiração de tokens. |
| **JWE (Encryption)** | Necessidade de confidencialidade extra em infraestrutura compartilhada. |
| **Network Policies** | Restrição de tráfego entre pods em clusters Kubernetes. |
