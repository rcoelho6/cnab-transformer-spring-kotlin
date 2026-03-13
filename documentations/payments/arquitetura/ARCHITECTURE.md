# Arquitetura — Visão Geral

Objetivo
- Entregar uma visão clara e navegável da arquitetura do componente responsável por transformar arquivos CNAB240 em JSON de domínio e orquestrar sua entrega a consumidores (REST/JMS) e retorno `.RET`.

Estrutura principal (Opção B)
- Domain (Domínio puro)
- Technical Core (Parser, Pusher, BankProfile)
- Application (Use-cases, Ports)
- Infrastructure (Adapters, wiring, boot)

Diagrama de alto nível (Mermaid)

```mermaid
flowchart LR
  subgraph Infra[Infrastructure]
    A1(HTTP Controller)
    A2(File Watch / SFTP)
    A3(S3 Event Handler)
    A4(JMS Listener)
    A5(Storage (S3/FS))
    A6(Pusher HTTP/JMS)
  end

  subgraph App[Application]
    B1(ProcessCnabUseCase)
    B2(SubscriptionService)
  end

  subgraph Core[Technical Core]
    C1(Parser CNAB -> Domain)
    C2(Pusher / Enqueue)
    C3(BankProfile Loader)
  end

  subgraph Domain[Domain]
    D1(Entities / ValueObjects)
    D2(ValidationRules)
  end

  A1 -->|POST upload| B1
  A2 -->|file detected| B1
  A3 -->|s3 event| B1
  A4 -->|message| B1

  B1 --> C1
  C1 --> D1
  D1 --> B1
  B1 --> C2
  C2 --> A6
  A6 --> A5

  B2 -->|manage| A6
  B2 -->|manage subs| A1
```

Notas
- O `Application` orquestra: recebe eventos de Infra, chama o `Parser` do Core, aplica regras de `Domain`, e usa `Pusher` para entregar/guardar/emitir eventos.
- O `Parser` é responsável por leitura em streaming (240-bytes), identificação de segmentos e mapeamento via `BankProfile`.

