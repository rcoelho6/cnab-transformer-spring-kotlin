# JSON — Troca de Dados (CNAB -> JSON)

Objetivo
- Documentar a proposta de entrega do JSON gerado pelo parser CNAB para aplicações terceiras que queiram se inscrever como consumidores.
- Suporta duas modalidades de entrega: REST (POST) ou JMS (fila).

Arquivos nesta pasta
- `INDEX.md` — você está aqui
- `REGISTRATION.md` — como se registrar (REST / JMS) e verificação
- `DELIVERY.md` — especificação de entrega (payloads, headers, semântica de resposta)
- `EXAMPLES.md` — exemplos práticos (curl, JSON, configuração JMS)

Segurança
- As práticas de segurança para a troca de JSONs internos foram consolidadas em `../../segurança/JSON-INTERNAL.md`.

Observação
- Os esquemas JSON de request/response e o formato do parser estão definidos em `documentations/payments/dominios/json/` (veja `JSON-SCHEMA.md`, `PARSER-JSON-CNAB.md` e `EXAMPLE-CNAB.JSON`). Esta documentação usa esses artefatos como fonte de verdade para os formatos.

Próximo passo
- Consulte `REGISTRATION.md` para aprender como uma aplicação pode se inscrever para receber os JSONs.

