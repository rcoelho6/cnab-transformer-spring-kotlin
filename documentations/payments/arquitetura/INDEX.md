# Arquitetura — componente CNAB / JSON

Esta pasta documenta a arquitetura do componente de transformação CNAB <-> JSON e integração com sistemas externos e internos.

Conteúdo
- `ARCHITECTURE.md` — visão geral da arquitetura, mapeamento de packages (Opção B) e diagramas de alto nível
- `HEXAGONAL.md` — explicação da Arquitetura Hexagonal aplicada ao componente, mapeamento Ports/Adapters
- `FLOWS.md` — fluxos detalhados (ingestão, parsing, enfileiramento, consumo, retorno `.RET`, delivery REST/JMS)

Observação
- A estrutura principal adotada segue a Opção B (Domain / Technical Core / Application / Infrastructure) conforme proposta em `documentations/propostas/CLASSIFICACAO_PACKAGES.md`.

