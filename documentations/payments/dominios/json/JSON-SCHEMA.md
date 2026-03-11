# ESQUEMA JSON (CNAB240) — GUIA SIMPLIFICADO E PRÁTICO

Objetivo
- Fornecer um JSON minimalista, claro e prático que represente arquivos CNAB240 para os tipos de pagamento suportados: PIX, BOLETO/CÓDIGO-BARRAS, TED, TRIBUTOS, TAXA-VEICULAR e DDA.
- Preservar apenas os campos essenciais para permitir remontar o arquivo posicional de 240 bytes através de um BankProfile.

Visão rápida (o que importa)
- Um `file` tem `header`, `batches[]` e `trailer`.
- Cada `batch` contém um único `paymentType` (regra: 1 lote = 1 tipo de pagamento).
- Cada `detail` contém campos mínimos comuns + um `extra` enxuto com o necessário para o tipo.
- O `BankProfile` (externo) contém offsets/formatos por banco/segmento para escrever posições no CNAB.

Checklist (para leitura imediata)
- [ ] 1 lote por tipo de pagamento (simplifica mapping e validação)
- [ ] Campos mínimos por detalhe: recordSeq, segment, paymentType, amount, date, payer{name,document}, extra
- [ ] Idempotência: use requestId / idempotencyKey
- [ ] Validar CPF/CNPJ, formatos de chave PIX, barcode length/checksum, códigos bancários
- [ ] Recalcular contagens (detailCount, recordCount) ao gerar CNAB

---

## Estrutura JSON (compacta)

```json
{
  "file": {
    "header": {"bankCode":"033","fileDate":"2026-03-10","layoutVersion":"240"},
    "batches": [
      {
        "header": {"batchNumber":1,"serviceType":"C","companyId":"12345678000199"},
        "paymentType": "BOLETO",
        "details": [ { /* detalhe mínimo */ } ],
        "trailer": {"detailCount": 1}
      }
    ],
    "trailer": {"batchCount":1,"recordCount":5}
  }
}
```

Notas visuais
- `paymentType` em `batch.header` ou imediatamente em `batch` indica o tipo homogêneo do lote.
- `details[]` deve conter só os registros necessários para esse tipo (ex.: P/Q para boletos).

---

## Regra importante: 1 LOTE = 1 TIPO DE PAGAMENTO

Por que?
- CNAB define lotes (service sections) por tipo de serviço; misturar pagamentos diferentes em um mesmo lote pode:
  - quebrar validações do banco;
  - exigir diferentes segmentos/offsets dentro do mesmo lote (complexo);
  - dificultar a geração de arquivos padronizados e a certificação com o banco.

Benefícios
- Simplifica `BankProfile` (mapeamento por lote/segmento)
- Facilita validações por lote (campos obrigatórios homogêneos)
- Reduz risco de rejeição pelo banco

Implementação prática
- Ao gerar JSON, agrupe automaticamente detalhes por `paymentType` em lotes separados.

---

## Campos mínimos por detalhe (a lista mínima e por que eles são obrigatórios)

Campos comuns (todo detalhe)
- `recordSeq` (int): posição sequencial no lote — necessário para ordenar/contar
- `segment` (string): segmento CNAB associado (ex.: "P","Q","T") — guia o mapping
- `paymentType` (string): PIX|BOLETO|BARCODE|TED|TRIBUTE|TAX_VEHICLE|DDA
- `amount` (int): valor em centavos — obrigatório para compor campos de valor
- `date` (string ISO YYYY-MM-DD): dueDate ou valueDate
- `payer` (object): { name, document } — identificação do pagador (necessária para cobrança/recibo)
- `requestId` (string, opcional mas recomendado): idempotência e rastreabilidade
- `extra` (object): campos específicos do tipo (ver abaixo)

Campos `extra` por tipo (mínimos absolutos)
- PIX: `{ "pixKey": "fulano@bank.com" }` ou `{ "txid": "..." }`
- BOLETO/BARCODE: `{ "barcode": "44digits" }` ou `{ "ourNumber": "...", "documentNumber": "..." }`
- TED: `{ "bankCode":"001","agency":"1234","account":"00012345","holderName":"Fulano","holderDocument":"12345678901" }`
- TRIBUTE: `{ "taxCode":"1500","taxpayerDocument":"12345678000199","reference":"202602" }`
- TAX_VEHICLE: `{ "vehicleId":"ABC1234","taxCode":"IPVA" }`
- DDA: `{ "nossoNumero":"000123","titleNumber":"20260001","statusHint":"APRESENTADO" }`

Motivo para `extra` minimal:
- Esses campos são suficientes para preencher as posições críticas em um segmento (valor, identificação, campo livre). Campos opcionais ou redundantes são omitidos para manter o JSON enxuto.

---

## Validações recomendadas (obrigatórias no input)

1. `file.header.bankCode` existe e tem 3 dígitos
2. Cada `batch` tem `paymentType` declarado
3. Cada `batch.details` contém ao menos 1 detalhe
4. Por detalhe:
   - `amount` > 0
   - `date` formato ISO e plausível
   - `payer.document` valida CPF/CNPJ
   - `extra` contém os campos necessários para o `paymentType`
5. Validar formatos específicos:
   - `pixKey`: email/phone/CPF/CNPJ/EVP (regex)
   - `barcode`: 44 dígitos e checksum (módulos conforme layout)
   - `bankCode`: 3 dígitos
6. Idempotência: `requestId` único por operação; rejeitar duplicados já finalizados

Referências locais para validações e layouts:
- `documentations/payments/cnab/COMPARE-SANTANDER-VS-BA.md` (diferenças de layout)
- `documentations/payments/santander-openworld/BAAS_PORTAL_OPEN.md` (contexto BaaS / Portal)

---

## Mapeamento: de onde vem cada campo no CNAB (resumo prático)

- Arquivo Header (tipo 0): `file.header.bankCode`, `file.header.fileDate`, `layoutVersion`
- Lote Header (tipo 1): `batches[].header.batchNumber`, `header.serviceType`, `header.companyId`
- Registro detalhe (tipo 3): `batches[].details[]` campos posicionais (segmento, valores, conta/agência, campo livre)
- Campo livre / complementos: normalmente extra (pixKey, barcode parcial, identificadores)
- Trailers (tipo 5/9): contadores — podem ser recalculados

Observação: posições exatas dependem do `BankProfile` (layout por banco). Sempre consulte o PDF do banco (ex.: Santander) para definir offsets.

---

## Como remontar o CNAB (passos claros)

1. Gerar File Header (registro tipo 0): preencher `bankCode`, `fileDate`, `layoutVersion` e campos fixos do layout.
2. Para cada `batch`:
   - Gerar Batch Header (tipo 1) com `batchNumber`, `serviceType`, `companyId`.
   - Validar que `batch.paymentType` é consistente com os `detail.segment`.
   - Para cada `detail`:
      a. Obter `mapping = bankProfile.getMapping(batch.serviceType, detail.segment)`
      b. Criar `record` com 240 espaços
      c. Para cada `field` do mapping preencher com `format(detail[fieldName])` (padding, zeros, trunc)
      d. Escrever `record` como bytes ISO-8859-1
   - Gerar Batch Trailer (tipo 5) com somatórios/contagens
3. Gerar File Trailer (tipo 9)

Pseudocódigo (resumido)

```kotlin
writeFileHeader(file.header)
for (batch in file.batches) {
  writeBatchHeader(batch.header)
  for (detail in batch.details) {
    mapping = bankProfile.getMapping(batch.paymentType, detail.segment)
    record = String(' ',240)
    for (f in mapping.fields) {
      value = formatField(detail, f)
      record = replace(record, f.start-1, f.length, value)
    }
    write(record.getBytes('ISO-8859-1'))
  }
  writeBatchTrailer(batch.trailer)
}
writeFileTrailer(file.trailer)
```

---

## Campos fixos / hardcoded (exemplos e justificativas)
- Tamanho do registro: 240 bytes — padrão CNAB240
- Tipo de registro (pos 1): 0/1/3/5/9 — fixo por linha
- Preenchimentos: espaços (alfanum) / zeros (numéricos)
- Valores de layout que não mudam entre arquivos (podem ser hardcoded no builder)

Justificativa: Keeping these hardcoded reduces JSON size and centralizes formatting logic no gerador, fazendo o `BankProfile` responsável apenas por offsets e formatos por campo.

---

## EXEMPLO: veja `EXAMPLE-CNAB.JSON` (1 batch por tipo)
- Use esse arquivo para testes unitários e para construir `BankProfile`.

---

## Próximos passos recomendados
- Gerar um `JSON Schema` formal (arquivo `.json`) para validação automática
- Criar `BankProfile` para Santander (YAML/JSON) com offsets de segmentos usados
- Construir ferramentas de conversão: JSON -> CNAB e CNAB -> JSON (preservar `rawRecord` quando necessário)
- Testes: criar amostras reais e integrar com sandbox Santander

---

Referências locais
- `documentations/payments/cnab/COMPARE-SANTANDER-VS-BA.md`
- `documentations/payments/santander-openworld/BAAS_PORTAL_OPEN.md`

*Fim.*
