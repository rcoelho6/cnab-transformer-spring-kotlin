# Proposta de Classes e Arquitetura Técnica — V2

Este documento apresenta a evolução da arquitetura do sistema de processamento de arquivos **CNAB 240**, integrando requisitos avançados de **segurança**, **validação** e a utilização de frameworks do ecossistema **Spring**.

## Arquitetura Técnica (Spring-Based Hexagonal)

A solução utiliza a **Arquitetura Hexagonal** para isolar o domínio, enquanto aproveita o poder do Spring para gerenciar a infraestrutura e a orquestração.

![Arquitetura Spring Hexagonal](images/spring_hexagonal_v2.jpg)

### Integração de Frameworks

| Framework | Utilização no Projeto |
| :--- | :--- |
| **Spring Boot** | Base da aplicação e gerenciamento de dependências. |
| **Spring Security** | Implementação de mTLS, OAuth2 e filtros de segurança para validação de HMAC. |
| **Spring Integration** | Orquestração do fluxo de arquivos (File Watcher) e integração de canais. |
| **Spring Data JPA** | Persistência de modelos de repositório e auditoria. |
| **Spring JMS** | Comunicação assíncrona com filas de mensagens para entrega de JSON. |
| **Spring Cloud Stream** | Abstração para múltiplos brokers de mensagens (opcional). |

## Pipeline de Processamento Seguro

O fluxo de processamento integra segurança e validação em cada etapa, garantindo que apenas dados legítimos e íntegros cheguem ao domínio.

![Pipeline de Segurança e Validação](images/validation_security_pipeline_v2.jpg)

## Detalhamento das Classes por Camada

### 1. Domain (Core)
O núcleo contém a lógica de negócio pura, independente de tecnologia.

-   **Entidades**: `CNABFile`, `Batch`, `TransactionRecord`.
-   **Serviços**: `CNABDomainService` (regras de negócio complexas), `ValidationService` (motor de regras de validação).
-   **Exceções**: `DomainException`, `ValidationException`.

### 2. Application (Use Cases)
Orquestra o fluxo de dados utilizando componentes Spring.

-   **Casos de Uso**: `ProcessCNABUseCase` (orquestrador principal), `SubscriptionUseCase` (gestão de webhooks/filas).
-   **DTOs**: `CNABRequestDTO`, `CNABResponseDTO`, `SubscriptionDTO`.
-   **Mappers**: `DomainToDTOMapper`.

### 3. Ports (Interfaces)
Define os contratos de entrada e saída.

-   **Inbound**: `CNABIngestionPort`, `SubscriptionPort`.
-   **Outbound**: `JSONDeliveryPort` (entrega para sistemas internos), `StoragePort` (S3/GCS), `AuditPort`.

### 4. Infrastructure (Adapters)
Implementações concretas utilizando frameworks Spring.

#### Inbound Adapters
-   `RESTController`: Endpoints protegidos por Spring Security (OAuth2/mTLS).
-   `FileWatcherAdapter`: Utiliza `Spring Integration` para monitorar diretórios.
-   `SecurityFilter`: Filtro customizado para validação de assinaturas HMAC.

#### Outbound Adapters
-   `JMSDeliveryAdapter`: Implementação de `JSONDeliveryPort` usando `Spring JMS`.
-   `RESTDeliveryAdapter`: Envio de webhooks com assinatura HMAC.
-   `S3StorageAdapter`: Integração com Cloud Storage.
-   `JPALoggingAdapter`: Auditoria e logs de processamento via Spring Data.

## Estrutura de Validação (Pipeline)

A classe `ValidationPipeline` orquestra uma lista de `Validator` que seguem a severidade definida:

1.  **IntegrityValidator**: Verifica tamanho (múltiplo de 240) e encoding.
2.  **HeaderValidator**: Valida código do banco e data do arquivo.
3.  **BusinessRuleValidator**: Valida algoritmos de CPF/CNPJ, chaves PIX e valores.
4.  **ConsistencyValidator**: Verifica somatórios de lotes e contagem de registros.

## Segurança e Idempotência

-   **HMACSigner**: Utilitário para gerar e validar assinaturas `X-Signature`.
-   **IdempotencyManager**: Utiliza um repositório (Redis ou DB) para verificar `X-Request-Id` e evitar reprocessamento.
-   **SecretProvider**: Integração com Spring Cloud Config ou AWS Secrets Manager para recuperação de chaves.

---

*Esta proposta visa criar um sistema resiliente, seguro e altamente escalável, pronto para integração com o ecossistema bancário moderno.*
