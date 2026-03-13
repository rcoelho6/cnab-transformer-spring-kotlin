# Troca de Dados — Índice

Esta seção agrupa os mecanismos de troca de dados relacionados a pagamentos, abrangendo desde o recebimento de arquivos **CNAB** até a entrega do **JSON** gerado pelo parser.

## Estrutura de Documentação

A troca de dados é dividida em duas frentes principais:

### 1. CNAB (Arquivos de Entrada)
Focada nos métodos de recebimento e processamento dos arquivos posicionais.
-   **[CNAB — Índice](cnab/INDEX.md)**
    -   **HTTP-BASE64**: [Overview](cnab/HTTP-BASE64/OVERVIEW.md) | [API-SPEC](cnab/HTTP-BASE64/API-SPEC.md) | [Exemplos](cnab/HTTP-BASE64/EXAMPLES.md)
    -   **Direct File**: [Overview](cnab/DIRECT-FILE/OVERVIEW.md) | [SFTP](cnab/DIRECT-FILE/SFTP.md) | [S3/GCS](cnab/DIRECT-FILE/S3-GCS.md) | [SMB](cnab/DIRECT-FILE/SMB.md)
    -   **Filewatch & Sched**: [Overview](cnab/FILEWATCH-SCHED/OVERVIEW.md) | [Filewatch](cnab/FILEWATCH-SCHED/FILEWATCH.md) | [Push-Notify](cnab/FILEWATCH-SCHED/PUSH-NOTIFY.md) | [Scheduler](cnab/FILEWATCH-SCHED/SCHEDULER.md)

### 2. JSON (Saída do Parser)
Focada na estrutura e entrega dos dados transformados para sistemas internos.
-   **[JSON — Índice](json/INDEX.md)**
    -   **Registro**: [Registration](json/REGISTRATION.md)
    -   **Entrega**: [Delivery](json/DELIVERY.md)
    -   **Exemplos**: [Examples](json/EXAMPLES.md)

## Segurança Consolidada

Para garantir a proteção dos dados em todos os canais, as recomendações de segurança foram centralizadas:
-   **[Segurança — Índice](../segurança/INDEX.md)**

---

![Fluxo CNAB para JSON](../images/cnab_to_json_flow.jpg)

*Diagrama ilustrativo do fluxo completo de transformação de dados.*
