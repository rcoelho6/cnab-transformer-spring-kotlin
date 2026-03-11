# Guia técnico simplificado para implementar parser CNAB240

Este guia descreve uma abordagem prática, por etapas, para implementar um parser CNAB240 focado nos tipos de pagamento: PIX, Boleto/Código de Barras, TED, Taxas Veiculares, Tributos e DDA. Também inclui sugestões de testes, arquitetura do parser (bank profiles) e pseudocódigo.

Visão geral da estratégia

1. Ler o arquivo em streaming, por registros de 240 bytes.
2. Identificar o tipo do registro (Header de arquivo, Header de lote, Detalhe/Segmento, Trailer de lote, Trailer de arquivo) lendo os campos iniciais (posições 1-3 tipo de registro etc.).
3. Para cada registro de detalhe, identificar o segmento (campo "segmento", normalmente posição 14) e delegar para um handler de segmento.
4. Os handlers de segmento populam objetos genéricos (DetailRecord) que são então transformados para modelos específicos do negócio (PIX, Boleto, TED, etc.) usando um BankProfile que define offsets e interpretações específicas por banco.

BankProfile

- Um `BankProfile` descreve os offsets e regras de interpretação para um banco específico (por exemplo, Santander). Pode ser representado por JSON/YAML e carregado na inicialização.
- Campos no `BankProfile`:
  - bancoCodigo (3 dígitos)
  - mapeamentos por segmento (ex.: segmento P -> { campos: { valor: {start: 153, length: 13}, data: {start: 147, length: 8}, ... } })
  - regras adicionais (ex.: campo livre contém txid -> regex)

Pseudocódigo (Kotlin-like)

```kotlin
fun parseCnab(file: InputStream, bankProfile: BankProfile): CnabFile {
  val reader = BufferedReader(InputStreamReader(file, StandardCharsets.ISO_8859_1))
  val cnab = CnabFile()
  while (true) {
    val line = readExactBytes(reader, 240) ?: break
    val recordType = line.substring(0,1) // posição 1 (1-based)
    when (recordType) {
      "0" -> cnab.header = parseFileHeader(line)
      "1" -> { // header de lote
        val loteNumber = line.substring(1,4)
        cnab.currentBatch = BatchHeader(...)
      }
      "3" -> { // registro detalhe
        val segmento = line.substring(13,14) // posição 14 (1-based)
        val handler = bankProfile.getSegmentHandler(segmento)
        val detail = handler.parse(line)
        cnab.currentBatch.details.add(detail)
      }
      "5" -> { // trailer de lote
        cnab.currentBatch.trailer = parseBatchTrailer(line)
        cnab.batches.add(cnab.currentBatch)
      }
      "9" -> { // trailer de arquivo
        cnab.trailer = parseFileTrailer(line)
      }
    }
  }
  return cnab
}
```

Nota sobre encoding
- Use ISO-8859-1 (Latin-1) para ler arquivos CNAB, a menos que o layout do banco especifique outro encoding.

Testes recomendados
- Unit tests para parsers de segmento: fornecer linhas de 240 bytes (strings) e validar campos extraídos.
- Testes de integração: real sample files do Santander e do layout padrão.
- Testes de regressão: garantir que alterações no BankProfile não quebrem parsing de arquivos anteriores.

Validações de segurança / robustez
- Tratar linhas menores que 240 bytes como erro ou pular com log.
- Validar campos críticos (valor, data, banco) e gerar alertas para arquivos não conformes.
- Rate-limit e processamento assíncrono para evitar sobrecarregar o pusher.

Estrutura de código sugerida (pacotes)
- `parser` - implementação do parser genérico
- `parser.handlers` - handlers por segmento
- `parser.bankprofiles` - carregadores e modelos de BankProfile
- `application` - caso de uso que usa parser e pusher

Exemplo de BankProfile (YAML)

```yaml
bancoCodigo: "033" # Santander
segments:
  P:
    fields:
      valor: {start: 152, length: 13}
      dataVencimento: {start: 146, length: 8}
      nossoNumero: {start: 61, length: 20}
    rules:
      codigoBarras: { composedFrom: ["campoX","campoY"] }
  Q:
    fields:
      sacadoNome: {start: 30, length: 40}
```

Test data
- Coloque amostras em `documentations/externals/cnab/samples/` e use para testes.

Conclusão
- Comece com um parser genérico + BankProfiles para cada banco. Isso minimiza duplicação e facilita a evolução quando aparecerem variações dos layouts.


