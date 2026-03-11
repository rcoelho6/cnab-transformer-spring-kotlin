# SEGMENT Z — EXTENSÃO DO JSON E PARSER

Objetivo: definir a extensão do JSON para incluir os campos necessários ao Segmento Z (respostas/retornos por detalhe) e descrever como parsear e montar a linha do Segmento Z no CNAB240.

Contexto rápido
- Segmento Z: usado por alguns bancos / layouts para informações de retorno, liquidação ou acompanhamento específico (p.ex. atualização de status, identificadores do banco, protocolos).
- Esse documento descreve: 1) como estender o JSON com campos para o Segmento Z; 2) como parsear a linha do Segmento Z para popular esses campos; 3) como gerar a linha do Segmento Z a partir do JSON de resposta.

---

## 1) Estrutura JSON estendida (response + segmentZ)

O JSON de resposta (após processamento/retorno do banco) deve conter o `segmentZ` dentro do detalhe ou como objeto de retorno associado:

```json
{
  "file": { ... },
  "responses": [
    {
      "requestId": "pix-0001",
      "batchNumber": 1,
      "recordSeq": 1,
      "segmentZ": {
        "zStatusCode": "00",
        "zStatusDesc": "LIQUIDADO",
        "zBankProtocol": "BR1234567890",
        "zSettlementDate": "2026-03-10",
        "zRaw": "...opcional-linha-240-bruta..."
      }
    }
  ]
}
```

Campos sugeridos para `segmentZ` (mínimo)

| Campo | Tipo | Obrigatório | Descrição |
|---|---:|:---:|---|
| zStatusCode | string | Sim | Código do status (ex: '00' = confirmado) |
| zStatusDesc | string | Sim | Texto descritivo do status |
| zBankProtocol | string | Opcional | Protocolo/ID do banco para a liquidação |
| zSettlementDate | date | Opcional | Data de liquidação / processamento |
| zRaw | string | Opcional | Linha raw do segmento Z (240 bytes) para auditoria |

Motivo: manter `zRaw` permite auditoria e reconciliação exata byte-a-byte quando necessário.

---

## 2) Como parsear a linha do Segmento Z (CNAB -> JSON)

Passos práticos:
1. Ler a linha de 240 bytes (registro detalhe com segmento 'Z' ou segmento específico do banco)
2. Validar comprimento = 240
3. Aplicar `bankProfile.segmentZ` (offsets definidos) para extrair campos:
   - zStatusCode <- substring(startCode-1, lengthCode)
   - zStatusDesc <- substring(startDesc-1, lengthDesc)
   - zBankProtocol <- substring(startProtocol-1, lengthProtocol)
   - zSettlementDate <- substring(startDate-1, lengthDate) -> format to ISO
4. Montar `segmentZ` no JSON de resposta (incluir `zRaw` se necessário)

Exemplo pseudocódigo (Kotlin-style)

```kotlin
fun parseSegmentZ(line:String, segmentZMapping: FieldMapping): SegmentZ {
  require(line.length == 240)
  val code = line.substring(segmentZMapping.code.start-1, segmentZMapping.code.length).trim()
  val desc = line.substring(segmentZMapping.desc.start-1, segmentZMapping.desc.length).trim()
  val proto = line.substring(segmentZMapping.protocol.start-1, segmentZMapping.protocol.length).trim()
  val dateRaw = line.substring(segmentZMapping.date.start-1, segmentZMapping.date.length).trim()
  val date = parseDate(dateRaw)
  return SegmentZ(code, desc, proto, date, zRaw = line)
}
```

---

## 3) Como montar a linha do Segmento Z (JSON -> CNAB)

Regras:
- Usar `BankProfile.segmentZ` para saber offsets e lengths
- Para cada campo do `segmentZ`:
  - formatar conforme tipo (codigo: left-pad / alfanum right-pad, data: YYYYMMDD)
  - inserir no `record` de 240 bytes
- Preencher posições não utilizadas com espaços
- Escrever em ISO-8859-1

Pseudocódigo resumido

```kotlin
fun buildSegmentZ(segmentZ: SegmentZ, mapping: FieldMapping): String {
  var record = StringBuilder(" ".repeat(240))
  record.replace(mapping.code.start-1, mapping.code.start-1 + mapping.code.length, pad(segmentZ.zStatusCode, mapping.code.length))
  record.replace(mapping.desc.start-1, mapping.desc.start-1 + mapping.desc.length, padRight(segmentZ.zStatusDesc, mapping.desc.length))
  // protocol, date, etc.
  return record.toString()
}
```

---

## 4) Regras operacionais e validações para o Segmento Z
- `zStatusCode` deve mapear para um conjunto conhecido (por ex.: 00=LIQUIDADO, 01=PENDENTE, 02=REJEITADO)
- `zSettlementDate` quando presente deve usar formato ISO
- Se `zRaw` presente, preferir dar audit trail e validar parsing re-runável

---

## 5) Exemplos e testes
- Inclua tests unitários que: leem uma linha Z de exemplo, parseiam para JSON e reconvertem para string, assertando igualdade (quando `zRaw` presente) ou equivalência sem whitespaces de padding.

---

## 6) Referências e notas
- Offsets exatos do Segmento Z vêm do `BankProfile` / PDF do banco (Santander) e variam por layout.
- Documentação de referência: `documentations/payments/cnab/COMPARE-SANTANDER-VS-BA.md`.

*Fim.*

