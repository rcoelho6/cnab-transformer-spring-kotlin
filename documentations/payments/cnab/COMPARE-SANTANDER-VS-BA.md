
# Comparação resumida: Layout Santander vs Layouts Bacen/Febraban (CNAB240)

Fontes encontradas na pasta `documentations/externals/cnab`:
- "Layout padrao CNAB240 V 10 11 - 21_08_2023.pdf" (provavelmente Febraban/Bacen ou layout padrão)
- "Pagamento-a-Fornecedores-Layout-CNAB-240-V.11.6.pdf" (layout de pagamento a fornecedores - versão 11.6)
- "Layout-Cobranca-240-posicoes-padrao-Santander-Multibanco-jul-2025-Portugues.pdf" (Santander — cobrança / boleto)

Resumo da comparação

1. Estrutura geral (semelhanças)
- Ambos seguem o padrão CNAB240: registro header (1), lotes (each with header, detail segments, trailer) e arquivo trailer.
- Os registros de detalhe são posicionalmente fixos. Campos como Agencia/Conta, NossoNumero, Valor, Data, Codigo do Segmento, etc., aparecem em posições padronizadas, mas com variações específicas por banco no conteúdo semântico e no uso de campos.
- Há uso comum de segmentos (por exemplo, segmento "P" para cobrança, segmentos para movimentação financeira) — nomenclaturas e uso seguem Febraban, mas bancos podem ter segmentos proprietários.

2. Principais diferenças observadas (Santander vs layout padrão)
- Santander detalha campos específicos para cobrança (boleto) e possui posições extras/descrições próprias para interpretação de código de barras e instruções de protesto/baixa automática.
- Campos relacionados à identificação do pagador e sacado podem ter tamanhos e tratamentos diferentes (por ex.: razão social vs nome curto) — atenção ao trimming e encoding.
- Santander costuma incluir campos para identifiers de integração do banco (IDs, códigos de cobrança) que não aparecem no layout mínimo do Bacen/Febraban.

3. Relevância para parsing
- Implementar uma camada de mapeamento por banco: um parser genérico que entende a estrutura CNAB240 e delegates para mappers/bank-profiles que extraem campos semânticos por banco.
- Evitar hardcode de offsets de alto nível sem um mapeamento por layout — centralizar posições em arquivos YAML/JSON por banco facilita manutenção.

4. Exemplo prático (como abordar diferenças)
- Parser principal: lê registros por linha (240 bytes), identifica tipo (header de arquivo, header de lote, detalhe segmento X, trailer) e cria objetos genéricos: FileHeader, BatchHeader, Detail(segmentId), BatchTrailer, FileTrailer.
- BankProfile (Santander): define offsets e interpretações para segmentos de cobrança (ex.: segmento P com campos extras para código de barras). Outro BankProfile (Genérico/Febraban) define offsets mínimos.

Recomendações
- Testar com amostras reais do Santander e exemplos do layout padrão.
- Implementar teste de compatibilidade que valida se um BankProfile corresponde a um arquivo (por exemplo, lendo campo do header que identifica banco/codigo).
- Documentar todas as exceções/variações detectadas durante os testes.

Links úteis
- PDF do Santander: `documentations/externals/cnab/Layout-Cobranca-240-posicoes-padrao-Santander-Multibanco-jul-2025-Portugues.pdf`
- Layout padrão / Febraban / Bacen: `documentations/externals/cnab/Layout padrao CNAB240 V 10 11 - 21_08_2023.pdf`


