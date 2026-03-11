# HTTP-BASE64 — Overview

Descrição
- Recebimento de arquivo CNAB via requisição HTTP POST com o conteúdo do arquivo codificado em Base64 no corpo JSON.
- Aplicável quando o cliente tem capacidade de chamar APIs REST e prefere transporte via HTTPS.

Arquivos nesta pasta
- `OVERVIEW.md` (você está aqui)
- `API-SPEC.md` — especificação da API (endpoints, headers, payload)
- `EXAMPLES.md` — exemplos de requests/responses
- `SECURITY.md` — recomendações de segurança (TLS, auth, rate-limit)

Vantagens
- Simplicidade de integração via REST
- Controle imediado de resposta (sync/async)
- Compatível com firewalls e infra HTTP

Desvantagens
- Uploads grandes podem ser problemáticos (timeouts)
- Necessidade de codificação Base64 aumenta ~33% o tamanho do payload

Modos de operação
- Síncrono: servidor retorna validação rápida (aceito/rejeitado) e `processingId`.
- Assíncrono: servidor retorna `202 Accepted` e `processingId`; validação ocorre em background; envio de webhook ao concluir.

Próximo: ver `API-SPEC.md` para detalhes de payload e headers.

