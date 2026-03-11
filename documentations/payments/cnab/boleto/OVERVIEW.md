
# Boleto / Código de Barra - Overview

Arquivos nesta pasta
- `overview.md` (você está aqui)
- `mapping.md` — mapeamento de campos para código de barras e informações do boleto
- `examples.md` — exemplos de registros de cobrança (Santander e layout padrão)
- `notes.md` — observações específicas por banco

Resumo
- Cobrança via boleto é um dos fluxos mais detalhados em CNAB240. O Santander possui um layout de cobrança com campos específicos para "nosso número", instruções de protesto, código de barras e informações de títulos.
- Para mapear boletos, os campos críticos são: número do documento, nosso número, código de barras (campo livre e/ou campos separados), valor do título, data de vencimento, sacado/pagador (nome e documento), instruções de cobrança.

Estratégia de parsing
- Identificar segmentos de cobrança (ex.: segmento P e Q no padrão de cobrança)
- Extrair campos do título e do sacado
- Reconstruir ou validar código de barras a partir do campo de 44 dígitos quando presente; alguns bancos apresentam componentes separados do código de barras


