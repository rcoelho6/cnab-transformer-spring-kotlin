# PARSER CNAB -> JSON (GUIA PRÁTICO)

Objetivo: descrever como implementar um parser que lê um arquivo CNAB240 (posicional) e gera o JSON minimal proposto.

Checklist de implementação
- [ ] Ler arquivo em streaming (240 bytes por registro)
- [ ] Identificar tipo de registro (pos 1): 0 (file header), 1 (batch header), 3 (detail), 5 (batch trailer), 9 (file trailer)
- [ ] Mapear campos posicionalmente usando um `BankProfile`
- [ ] Construir `file.header`, `batches[]`, `details[]` no JSON
- [ ] Validar e emitir erros (invalid length, field parse error)

## Passo a passo (alto nível)
1. Abra o arquivo como stream; leia blocos de 240 bytes
2. Para cada bloco:
   - Leia `recordType = substring(0,1)` (1-based pos1)
   - switch(recordType):
     - '0' -> parseFileHeader(line)
     - '1' -> parseBatchHeader(line)
     - '3' -> parseDetail(line, currentBatch)
     - '5' -> parseBatchTrailer(line)
     - '9' -> parseFileTrailer(line)
3. `parseDetail`: identifique `segment = substring(13,14)` (posição 14 typical)
   - use `bankProfile` para mapear offsets para este segmento
   - extraiga campos essenciais (amount, dates, payer fields, campo-livre)
   - normalize fields (trim, parse numeric, parse date)
   - append to `currentBatch.details`
4. Ao final do lote, calcular `detailCount` se faltante

## Exemplos de mapeamento (pseudocódigo)

```kotlin
fun parseDetail(line:String, bankProfile: BankProfile): Detail {
  val segment = line.substring(13,14)
  val mapping = bankProfile.getSegmentMapping(segment)
  val detail = Detail()
  detail.segment = segment
  detail.amount = parseNumeric(line.substring(mapping.amount.start-1, mapping.amount.length))
  detail.date = parseDate(line.substring(mapping.date.start-1, mapping.date.length))
  detail.payer = Payer(name=line.substring(mapping.payerName.start-1, mapping.payerName.length).trim(), document=...)
  detail.extra = mapping.extractExtras(line)
  return detail
}
```

## BankProfile (essencial)
- Deve conter, por banco e por segmento, a lista de campos com `start` (1-based) e `length`.
- Pode ser YAML/JSON e versionado junto ao código.

Exemplo de entradas do BankProfile (YAML - exemplo simplificado)

```yaml
033:
  segments:
    P:
      amount: {start:153, length:13}
      date: {start:147, length:8}
      payerName: {start:30, length:40}
      payerDocument: {start:70, length:14}
      extra:
        barcode: {start:120, length:44}
```

## Validações no parse
- Linha tem 240 bytes
- Campos numéricos parseáveis
- Datas válidas
- Campos obrigatórios por `paymentType` presentes

## Saída
- JSON conforme `JSON-SCHEMA.md` e `EXAMPLE-CNAB.JSON`.

---

Dicas operacionais
- Use encoding ISO-8859-1 ao ler bytes
- Trate espaços e zeros conforme tipo do campo
- Logar `requestId` se encontrado para facilitar rastreio


