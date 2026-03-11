# PARSER JSON -> CNAB (GUIA PRÁTICO)

Objetivo: descrever o builder que converte o JSON minimal de volta para CNAB240, incluindo campos fixos/hardcoded e mapeamento posicional via `BankProfile`.

Checklist de implementação
- [ ] Validar JSON contra regras mínimas
- [ ] Recalcular contadores (detailCount, recordCount)
- [ ] Para cada lote, gerar header, detalhes (240 bytes) e trailer
- [ ] Usar `BankProfile` para offsets e formatos (padding/trunc)
- [ ] Escrever arquivo em ISO-8859-1

## Passo a passo (alto nível)
1. Validar JSON (presence, tipos, regras por paymentType)
2. Recalcular contagens: para cada batch, `detailCount = batch.details.size`
3. Gerar File Header (registro tipo 0)
4. Para cada batch:
   - Gerar Batch Header (registro tipo 1)
   - Para cada detail:
     - mapping = bankProfile.getMapping(batch.paymentType, detail.segment)
     - record = String(' ', 240)
     - for field in mapping.fields: val = formatField(detail[fieldName], field.format); place into record
     - write record + newline (or diretamente concatenado ao stream)
   - Gerar Batch Trailer (tipo 5)
5. Gerar File Trailer (tipo 9)

## Formatação de campo (regras)
- numéricos: zeros à esquerda (ex.: valor 123 -> "00000000000123")
- alfanuméricos: espaço à direita (trim/pad)
- datas: YYYYMMDD (sem separador)
- campos faltantes: preencher com espaços/zeros conforme tipo

## Exemplo de `formatField` (pseudo)
```kotlin
fun formatField(value:Any?, fieldDef: FieldDef): String {
  if (value == null) return fieldDef.blank()
  when(fieldDef.type) {
    NUMERIC -> return value.toString().padLeft(fieldDef.length, '0')
    ALPHA -> return value.toString().padRight(fieldDef.length, ' ')
    DATE -> return formatDate(value)
  }
}
```

## BankProfile (requisito)
- `BankProfile` define, por segmento, `fields: [{name, start, length, type}]`.
- Pode conter regras de composição (ex.: barcode = concat(campoA,campoB)) e validações (checksum).

## Operações pós-build
- Validar tamanho do arquivo (n * 240 bytes)
- Validar checksums/contagens do trailer
- Assinar/criptografar se necessário (por requisitos do banco)

---

### Observações finais
- Mantenha o `BankProfile` versionado para suportar variações de layout entre bancos e versões.
- Para debugging, opcionalmente incluir `rawRecord` no JSON de saída para comparação com o build.


