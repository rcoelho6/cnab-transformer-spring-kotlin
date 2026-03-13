# Proposta de Classes e Arquitetura Técnica — V2 (Revisada)

Este documento apresenta a arquitetura técnica do sistema de processamento de arquivos **CNAB 240**, refletindo a dinâmica real do projeto: **Application como camada de Adapters**, **Services como detentores dos Casos de Uso** e **Infrastructure como suporte técnico externo**.

## Arquitetura Técnica (Spring-Based Hexagonal)

A solução utiliza a **Arquitetura Hexagonal** para isolar o domínio, com uma clara separação de responsabilidades entre as camadas.

![Arquitetura Spring Hexagonal](images/spring_hexagonal_v3.jpg)

### Dinâmica das Camadas

| Camada | Responsabilidade | Componentes Spring |
| :--- | :--- | :--- |
| **Domínio (Core)** | Contém as entidades puras e as interfaces de portas. | Entidades, Value Objects, Ports. |
| **Serviços (Services)** | Onde residem os **Casos de Uso** e a lógica de orquestração. | `@Service`, `@Transactional`. |
| **Aplicação (Adapters)** | Atua como a camada de **Adaptadores** (Entrada/Saída). | `@RestController`, `@JmsListener`, `@Component`. |
| **Infraestrutura (Suporte)** | Fornece suporte técnico e integração com o mundo externo. | `@Configuration`, `Repositories`, `Mappers`, `Parsers`. |

## Pipeline de Processamento Seguro

O fluxo de processamento integra segurança e validação, orquestrado pelos serviços de domínio.

![Pipeline de Segurança e Validação](images/validation_security_pipeline_v3.jpg)

## Detalhamento das Classes por Camada

### 1. Domínio (`domain`)
O núcleo do sistema, contendo as regras que não mudam.
-   **`domain.entities`**: `CNABFile`, `Batch`, `TransactionRecord`.
-   **`domain.ports`**: Interfaces `in` (ex: `CNABIngestionPort`) e `out` (ex: `JSONDeliveryPort`).
-   **`domain.services`**: Contém os **Casos de Uso** como `ProcessCNABService` e `ValidationService`.

### 2. Aplicação (`application`) — Camada de Adaptadores
Responsável por receber e enviar dados, adaptando-os para o domínio.
-   **`application.apis`**: Contém os **Controllers** (REST) e **Clientes** que iniciam o fluxo.
-   **`application.mqs`**: Contém os **Consumers** e **Producers** para integração via mensageria.
-   **`application.dtos`**: Objetos de transferência de dados para comunicação externa.

### 3. Infraestrutura (`infrastructure`) — Suporte Técnico
Complementa os adaptadores e portas com implementações técnicas.
-   **`infrastructure.configs`**: Configurações do Spring, Segurança (mTLS/OAuth2) e Beans.
-   **`infrastructure.repositories`**: Implementações de persistência e modelos de banco de dados.
-   **`infrastructure.mappers`**: Lógica de conversão entre DTOs, Entidades e Modelos.
-   **`infrastructure.formatters`**: Utilitários de normalização e parsing de arquivos.

## Segurança e Validação

-   **Filtros de Segurança**: Implementados na camada de aplicação/infra para validar assinaturas HMAC e tokens.
-   **Pipeline de Validação**: Executado dentro dos serviços de domínio antes do processamento principal.
-   **Idempotência**: Gerenciada via infraestrutura para garantir que o mesmo `X-Request-Id` não seja processado duas vezes.

---

*Esta estrutura garante que o sistema seja modular, permitindo que a tecnologia de transporte (REST/MQ) ou de persistência mude sem afetar as regras de negócio centrais.*
