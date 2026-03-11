
# CNAB - Guia simplificado para parser

Este diretório contém um guia funcional e técnico simplificado para implementar a leitura e o parser de arquivos CNAB (formato CNAB240) com foco em pagamentos: PIX (todos os tipos), Código de Barra / Boleto, TED, Taxas Veiculares, Tributos e DDA.

Estrutura desta pasta

- `index.md` (você está aqui) — visão geral e links para cada tipo de pagamento
- `compare-santander-vs-bacen.md` — comparativo resumido entre os PDFs do Santander e os layouts legais/Febraban/Bacen
- Pastas por tipo de pagamento (cada uma com páginas técnicas e exemplos):
  - `pix/` — PIX (todos os tipos)
  - `boleto/` — Código de Barra / Boleto
  - `ted/` — Transferências (TED/DOC)
  - `taxa-veicular/` — Taxas Veiculares
  - `tributos/` — Tributos
  - `dda/` — DDA (Débito Direto Autorizado / Documentos Arquivados)

Objetivo deste guia

- Fornecer uma visão prática e técnica mínima para começar a implementar um parser CNAB240.
- Apontar decisões de design: streaming por registro fixo, extensibilidade por banco, testes com amostras.
- Fornecer por tipo de pagamento as informações necessárias para mapear campos, regras de validação e exemplos de parsing.

Observações importantes

- Existem variações entre bancos (por isso criamos um comparativo Santander vs Bacen). Sempre confirme posições exatas nos PDFs (layouts fornecidos) antes de implementar extrações com offsets rígidos.
- Este guia é intencionalmente prático e enxuto — para uso como referência por desenvolvedores que precisam construir o parser rapidamente.

Links rápidos

- [Comparação Santander vs Bacen](./COMPARE-SANTANDER-VS-BACE.md)
- [PIX](./pix/OVERVIEW.md)
- [Boleto / Código de Barra](./boleto/OVERVIEW.md)
- [TED / Transferências](./ted/OVERVIEW.md)
- [Taxas Veiculares](./taxa-veicular/OVERVIEW.md)
- [Tributos](./tributos/OVERVIEW.md)
- [DDA](./dda/OVERVIEW.md)

